import torch
from tqdm import tqdm 
from torch.amp import autocast                

def train_epoch(model, data_loader, loss_fn, optimizer, device, scaler, 
                epoch=0, save_path=None, save_steps=1000): # Додали нові аргументи
    model.train()
    losses = []
    
    for step, batch in enumerate(tqdm(data_loader, desc="Training epoch")):
        input_ids = batch['input_ids'].to(device)
        attention_mask = batch['attention_mask'].to(device)
        labels = batch['labels'].to(device)
        
        with torch.amp.autocast(device_type=device.type):  
            outputs = model(input_ids, attention_mask)
            loss = loss_fn(outputs, labels)
            
        losses.append(loss.item())
        
        optimizer.zero_grad(set_to_none=True)
        scaler.scale(loss).backward()
        scaler.unscale_(optimizer)
        torch.nn.utils.clip_grad_norm_(model.parameters(), max_norm=1.0)
        scaler.step(optimizer)
        scaler.update()
        
        # --- НОВА ЛОГІКА ПРОМІЖКОВОГО ЗБЕРЕЖЕННЯ ---
        if save_path and (step + 1) % save_steps == 0:
            interim_path = save_path.replace('.bin', '_latest.bin')
            checkpoint = {
                'epoch': epoch,
                'step': step, # Зберігаємо поточний крок
                'model_state_dict': model.state_dict(),
                'optimizer_state_dict': optimizer.state_dict(),
                'scaler_state_dict': scaler.state_dict(),
                # Ми не знаємо тут best_val_loss, тому зберігаємо як є
            }
            torch.save(checkpoint, interim_path)
    
    return sum(losses) / len(losses)


def eval_model(model, data_loader, loss_fn, device):
    """
    Optimized validation loop to prevent OOM crashes.
    """
    model = model.eval()
    
    losses = []
    all_labels = [] 
    all_probs = [] 
    
    with torch.no_grad():
        for batch in tqdm(data_loader, desc="Validation"):
            
            input_ids = batch['input_ids'].to(device)
            attention_mask = batch['attention_mask'].to(device)
            labels = batch['labels'].to(device)
            
            with autocast(device_type=device.type):
                outputs = model(input_ids, attention_mask)
                loss = loss_fn(outputs, labels)
                
            losses.append(loss.item())
            
            probs = torch.sigmoid(outputs.float())
            
            all_labels.append(labels.cpu().numpy())
            all_probs.append(probs.cpu().numpy())

    y_true_t = torch.cat([labels.cpu() for labels in all_labels])
    y_probs_t = torch.cat([probs.cpu() for probs in all_probs])
    
    avg_loss = sum(losses) / len(losses)
    
    print("\n Шукаємо оптимальний поріг...")
    thresholds = torch.arange(0.1, 0.95, 0.05)
    
    best_f1 = 0.0
    best_threshold = 0.5
    
    sum_y_true = y_true_t.sum().item()

    for t in thresholds:
        # Швидка конвертація ймовірностей у бінарні предсказання за поточним порогом
        y_pred_t = (y_probs_t > t).int()
        
        # Рахуємо кількість істинних позитивів (TP) для поточного порогу
        tp = (y_pred_t * y_true_t).sum().item()
        
        # Рахуємо кількість предсказаних позитивів (TP + FP) для поточного порогу
        sum_y_pred = y_pred_t.sum().item()
        
        # Рахуємо F1-мікро для поточного порогу
        denominator = sum_y_pred + sum_y_true
        if denominator == 0:
            f1 = 0.0
        else:
            f1 = 2.0 * tp / denominator
            
        if f1 > best_f1:
            best_f1 = f1
            best_threshold = t.item()
            
    print(f"F1 (micro) = {best_f1:.4f} на порозі = {best_threshold:.4f}")

    y_pred_best = (y_probs_t > best_threshold).int()
    
    tp_best = (y_pred_best * y_true_t).sum().item()
    fp_best = (y_pred_best * (1 - y_true_t)).sum().item()
    fn_best = ((1 - y_pred_best) * y_true_t).sum().item()
    
    precision = tp_best / (tp_best + fp_best) if (tp_best + fp_best) > 0 else 0.0
    recall = tp_best / (tp_best + fn_best) if (tp_best + fn_best) > 0 else 0.0
    
    return avg_loss, best_f1, precision, recall, best_threshold