import argparse 

import os
import config
import data_utils
import dataset
from dataset import TagCollator
import model_pretrained
import model_scratch
import train 

from transformers import AutoTokenizer
from torch.utils.data import DataLoader, random_split
import torch
import torch.nn as nn
import torch.optim as optim
from torch.optim.lr_scheduler import ReduceLROnPlateau
import numpy as np
import random

def set_seed(seed_value):
    """Вставить сід для повторюваності результатів."""
    random.seed(seed_value)
    np.random.seed(seed_value)
    torch.manual_seed(seed_value)
    if torch.cuda.is_available():
        torch.cuda.manual_seed_all(seed_value)
        torch.backends.cudnn.deterministic = True
        torch.backends.cudnn.benchmark = False


def run(args=None):
    if args:
        print("\n--- Застосування налаштувань з Адмін-панелі ---")
        
        if args.scratch: 
            config.USE_PRETRAINED_MODEL = False
        
        if args.model_name:
            config.MODEL_NAME = args.model_name
        
        if args.batch_size:
            config.BATCH_SIZE = args.batch_size
            
        if args.epochs:
            config.NUM_EPOCHS = args.epochs
            
        if args.lr:
            config.LEARNING_RATE = args.lr
            
        mode_str = "FROM SCRATCH" if not config.USE_PRETRAINED_MODEL else f"PRE-TRAINED ({config.MODEL_NAME})"
        print(f"Режим: {mode_str}")
        print(f"Batch Size: {config.BATCH_SIZE}")
        print(f"Epochs: {config.NUM_EPOCHS}")
        print(f"Learning Rate: {config.LEARNING_RATE}")
        print("--------------------------------------------------\n")

    set_seed(config.SEED)
    df = data_utils.load_and_clean_data(
        config.DATA_FILE,
        config.TEXT_COLUMN_NAME,
        config.TAG_COLUMN_NAME,
        config.PARSED_TAG_COLUMN 
    )
    
    df = data_utils.filter_rare_tags(
        df,
        config.PARSED_TAG_COLUMN,     
        config.FILTERED_TAG_COLUMN,    
        config.MIN_TAG_FREQUENCY       
    )

    df = df.sample(
        frac=1.0,
        random_state=config.SEED 
    ).reset_index(drop=True)

    print(f"Будуємо карти тегів з '{config.FILTERED_TAG_COLUMN}'...")

    if(config.TAG_MAP_FILE and os.path.exists(config.TAG_MAP_FILE)):
        print(f"Завантаження готових карт тегів з {config.TAG_MAP_FILE}...")
        tag_to_id, id_to_tag, num_tags, best_thresh_loaded = data_utils.load_tag_maps(config.TAG_MAP_FILE)
        print(f"Знайдено {num_tags} тегів у збережених картах.")
    else:
        tag_to_id, id_to_tag, num_tags, best_thresh_loaded = data_utils.build_tag_maps(
            df, config.FILTERED_TAG_COLUMN 
        )

    print(f"Фінальне число тегів для навчання: {num_tags}")
    data_utils.save_tag_maps(tag_to_id, id_to_tag, num_tags, config.TAG_MAP_FILE)

    print("Розрахунок 'pos_weight' на чистих даних...")
    
    total_positive_tags = df[config.FILTERED_TAG_COLUMN].apply(len).sum()

    total_elements = len(df) * num_tags 
    total_negative_tags = total_elements - total_positive_tags
    
    
    print("\n---  Баланс класів ---")
    print(f"Всього рядків у Dataset: {len(df)}")
    print(f"Всього '1' (позитивних): {total_positive_tags}")
    print(f"Всього '0' (негативних): {total_negative_tags}")
    print("--------------------------------------------------\n")

    print(f"Ініціалізація токенізатора {config.MODEL_NAME}...")
    tokenizer = AutoTokenizer.from_pretrained(config.MODEL_NAME)
    collator = TagCollator(tokenizer=tokenizer, max_len=config.MAX_LENGTH)
    
    print("Створення Dataset...")
    full_dataset = dataset.TagDataset(
        dataframe=df,
        tag_map=tag_to_id,
        num_all_tags=num_tags,
        text_col=config.TEXT_COLUMN_NAME,
        tag_list_col=config.FILTERED_TAG_COLUMN 
    )

    print(f"Розділення даних...")
    total_size = len(full_dataset)
    val_size = int(total_size * config.VALIDATION_SPLIT)
    train_size = total_size - val_size
    train_dataset, val_dataset = random_split(
        full_dataset, 
        [train_size, val_size],
        generator=torch.Generator().manual_seed(config.SEED) 
    )
    
    print(f"Train: {len(train_dataset)} семплів / Val: {len(val_dataset)} семплів")
    
    NUM_WORKERS = 4 if torch.cuda.is_available() else 0
    train_loader = DataLoader(
        train_dataset, batch_size=config.BATCH_SIZE, shuffle=True,
        num_workers=NUM_WORKERS, pin_memory=True,
        collate_fn=collator
    )
    val_loader = DataLoader(
        val_dataset, batch_size=config.BATCH_SIZE, shuffle=False,
        num_workers=NUM_WORKERS, pin_memory=True,
        collate_fn=collator
    )

    print("\n--- Ініціалізація моделі ---")
    vocab_size = tokenizer.vocab_size
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    print(f"Використовуємо пристрій: {device}")

    if config.USE_PRETRAINED_MODEL:
        print(f"Завантаження PRE-TRAINED моделі: {config.MODEL_NAME}")
        classifier = model_pretrained.EncoderOnlyClassifier(
            model_name=config.MODEL_NAME,
            num_tags=num_tags
        ).to(device)

    else:
        print(f"Створення моделі FROM SCRATCH")
        print(f"(d_model={config.D_MODEL}, n_layers={config.N_LAYERS}, n_heads={config.N_HEADS})")
        classifier = model_scratch.EncoderOnlyClassifier(
            vocab_size=vocab_size, 
            num_tags=num_tags, 
            d_model=config.D_MODEL,
            n_layers=config.N_LAYERS, 
            n_heads=config.N_HEADS,
            ffn_hid_dim=config.FFN_HID_DIM, 
            dropout_p=config.DROPOUT_P,
            max_len=config.MAX_LENGTH
        ).to(device)
        print("Модель успішно створена.")
    
    print("\n--- Налаштування функції втрат, оптимізатора та планувальника LR ---")
    if total_positive_tags == 0:
        pos_weight_scalar = 1.0 
    else:
        pos_weight_scalar = min(total_negative_tags / total_positive_tags, config.POS_WEIGHT)
    
    pos_weight_tensor = torch.tensor([pos_weight_scalar], device=device)
    loss_fn = nn.BCEWithLogitsLoss(pos_weight=pos_weight_tensor).to(device)
    
    optimizer = optim.AdamW(classifier.parameters(), lr=config.LEARNING_RATE)
    
    scheduler = ReduceLROnPlateau(
            optimizer, mode='min', factor=0.1, patience=1, 
        )
    
    print(f"Функція втрат: BCEWithLogitsLoss (розрахунковий pos_weight={pos_weight_scalar:.2f})")
    
    scaler = torch.GradScaler(enabled=(device.type == 'cuda'))
    print(f"AMP (Mixed Precision) enabled: {scaler.is_enabled()}")
    start_epoch = 0
    best_val_loss = float('inf') 

    if args and args.resume:
        if os.path.exists(config.BEST_MODEL_PATH):
            print(f"\n--- Відновлення навчання з чекпоінту: {config.BEST_MODEL_PATH} ---")
            
            checkpoint = torch.load(config.BEST_MODEL_PATH, map_location=device)
            
            classifier.load_state_dict(checkpoint['model_state_dict'])
            optimizer.load_state_dict(checkpoint['optimizer_state_dict'])
            scheduler.load_state_dict(checkpoint['scheduler_state_dict'])
            
            if 'scaler_state_dict' in checkpoint:
                scaler.load_state_dict(checkpoint['scaler_state_dict'])
                
            start_epoch = checkpoint['epoch'] + 1
            best_val_loss = checkpoint.get('best_val_loss', float('inf'))
            
            print(f"Успішно відновлено! Продовжуємо з епохи {start_epoch + 1}, кращий val_loss: {best_val_loss:.4f}")
        else:
            print(f"\n[УВАГА] Чекпоінт {config.BEST_MODEL_PATH} не знайдено! Починаємо навчання з нуля.")
    print("\n--- ПОЧАТОК НАВЧАННЯ ---")

    for epoch in range(start_epoch,config.NUM_EPOCHS):
        print(f"\n--- Епоха {epoch + 1} / {config.NUM_EPOCHS} ---")
        current_lr = optimizer.param_groups[0]['lr']
        print(f"Поточний Learning Rate: {current_lr}")
        
        train_loss = train.train_epoch(
            classifier, 
            train_loader, 
            loss_fn, 
            optimizer, 
            device, 
            scaler,
            epoch=epoch,                           # Передаємо номер епохи
            save_path=config.BEST_MODEL_PATH,      # Базовий шлях
            save_steps=1000                        # Зберігати кожні 1000 батчів
        )
        print(f"Середній Train Loss: {train_loss:.4f}")
        
        val_loss, f1, precision, recall, best_thresh = train.eval_model(
            classifier, val_loader, loss_fn, device
        )
        
        print(f"Середній Validation Loss: {val_loss:.4f}")
        print(f"Validation F1 (micro): {f1:.4f}")
        print(f"Validation Precision (micro): {precision:.4f}")
        print(f"Validation Recall (micro): {recall:.4f}")

        print(f"Оптимальний поріг: {best_thresh:.4f}")

        scheduler.step(val_loss)
        
        if val_loss < best_val_loss:
            best_val_loss = val_loss
            print(f"Збереження найкращої моделі (val_loss: {best_val_loss:.4f})...")
            
            checkpoint = {
                'epoch': epoch,
                'model_state_dict': classifier.state_dict(),
                'optimizer_state_dict': optimizer.state_dict(),
                'scheduler_state_dict': scheduler.state_dict(),
                'scaler_state_dict': scaler.state_dict(), 
                'best_val_loss': best_val_loss,
                'best_thresh': best_thresh
            }
            
            torch.save(checkpoint, config.BEST_MODEL_PATH)
            
            data_utils.save_tag_maps(
                tag_to_id, 
                id_to_tag, 
                num_tags, 
                config.TAG_MAP_FILE,
                best_threshold=best_thresh 
            )

    print("\n---  НАВЧАННЯ ЗАВЕРШЕНО ---")


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Запуск навчання класифікатора тегів.")
    
    parser.add_argument('--scratch', action='store_true', help='Навчати модель з нуля (model_scratch.py)')
    parser.add_argument('--model_name', type=str, help='Ім\'я моделі з HuggingFace (наприклад distilbert-base-multilingual-cased)')
    parser.add_argument('--batch_size', type=int, help='Розмір батчу')
    parser.add_argument('--epochs', type=int, help='Кількість епох')
    parser.add_argument('--lr', type=float, help='Learning Rate')
    parser.add_argument('--resume', action='store_true', help='Відновити навчання з останнього збереженого чекпоінту')

    args = parser.parse_args()
    
    run(args)