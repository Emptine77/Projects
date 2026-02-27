# predict.py
"""
Скрипт для инференса (предсказания) с использованием обученной модели.
"""

import torch
import config
import data_utils
import model_pretrained
import model_scratch
from transformers import AutoTokenizer # ❗️ Используем AutoTokenizer

# ❗️ Глобальный порог УДАЛЕН. Он будет загружен из файла.

def load_inference_artifacts(device):
    """
    Загружает все необходимое для предсказания:
    - Карту тегов (tag_map.json) И ЛУЧШИЙ ПОРОГ
    - Токенизатор
    - Модель с весами (best_model.bin)
    """
    
    print("--- Загрузка артефактов ---")
    
    # 1. Загрузка карты тегов И ПОРОГА
    print(f"Загрузка карты тегов из {config.TAG_MAP_FILE}...")
    # ❗️ Теперь функция возвращает 4 значения
    tag_to_id, id_to_tag, num_tags, best_threshold = data_utils.load_tag_maps(config.TAG_MAP_FILE)
    
    # 2. Загрузка токенизатора
    print(f"Загрузка токенизатора {config.MODEL_NAME}...")
    tokenizer = AutoTokenizer.from_pretrained(config.MODEL_NAME) # ❗️ AutoTokenizer
    vocab_size = tokenizer.vocab_size
    
    # 3. Инициализация АРХИТЕКТУРЫ модели
    print("Инициализация архитектуры модели...")

    # ❗️❗️❗️ НОВЫЙ БЛОК ВЫБОРА МОДЕЛИ ❗️❗️❗️
    if config.USE_PRETRAINED_MODEL:
        print(f"Загрузка PRE-TRAINED архитектуры: {config.MODEL_NAME}")
        classifier = model_pretrained.EncoderOnlyClassifier(
            model_name=config.MODEL_NAME,
            num_tags=num_tags
        ).to(device)

    else:
        print(f"Создание архитектуры С НУЛЯ (FROM SCRATCH)")
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
    # 4. Загрузка ВЕСОВ модели
    print(f"Загрузка весов из {config.BEST_MODEL_PATH}...")
    classifier.load_state_dict(torch.load(config.BEST_MODEL_PATH, map_location=device))
    
    # ❗️❗️❗️ Перевод модели в режим .eval()
    classifier.eval()
    
    print("✅ Артефакты успешно загружены.")
    # ❗️ Возвращаем 4 значения
    return classifier, tokenizer, id_to_tag, best_threshold

def predict_text(text, model, tokenizer, id_to_tag, device, threshold, max_len=config.MAX_LENGTH):
    """
    Делает предсказание для одной строки текста.
    
    Args:
        threshold (float): Порог для отсечения (загружен из файла)
    """
    # 1. Токенизация (точно так же, как в Dataset)
    encoding = tokenizer.encode_plus(
        text,
        add_special_tokens=True, 
        max_length=max_len,
        padding='max_length',
        truncation=True,
        return_token_type_ids=False, # ❗️ Мы убрали их в Шаге 1
        return_attention_mask=True,
        return_tensors='pt',
    )
    
    # 2. Перемещаем тензоры на устройство
    input_ids = encoding['input_ids'].to(device)
    attention_mask = encoding['attention_mask'].to(device)

    
    # 3. Предсказание (без градиентов)
    with torch.no_grad():
        logits = model(input_ids, attention_mask)
    
    # 4. Постобработка
    probabilities = torch.sigmoid(logits).squeeze(0) # (num_tags)
    
    print(f"\n--- ДИАГНОСТИКА ВЕРОЯТНОСТЕЙ ---")
    print(f"Максимальная вероятность: {probabilities.max().item():.6f}")
    print(f"Минимальная вероятность: {probabilities.min().item():.6f}")
    print(f"Порог для предсказания: {threshold:.4f} (загружен из {config.TAG_MAP_FILE})")
    print(f"--- КОНЕЦ ДИАГNOСТИКИ ---\n")
    
    # 5. Отбор тегов
    predicted_tags = []
    
    probabilities_cpu = probabilities.cpu().numpy()
    
    for i, prob in enumerate(probabilities_cpu):
        # ❗️ Используем загруженный 'threshold'
        if prob > threshold:
            tag_name = id_to_tag[str(i)] # id_to_tag из JSON, ключи - строки
            predicted_tags.append(tag_name)
            
    return predicted_tags

# --- Точка входа ---
if __name__ == '__main__':
    
    DEVICE = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    
    # ❗️ Теперь возвращается 4 значения
    model, tokenizer, id_to_tag, PREDICTION_THRESHOLD = load_inference_artifacts(DEVICE)
    
    # --- Тестируем ---
    print("\n--- 🤖 Тестирование предсказаний ---")
    
    test_text_2 = 'This year featured 4 milestone releases for PyTorch in the 2.2, 2.3, 2.4 and 2.5 releases. We observed the release of various hallmark features like AOTInductor, FlashAttention-2 support, Tensor Parallelism, a new Python Custom Operator API, and the introduction of FlexAttention. Engineers from across PyTorch Foundation member companies have also come together to introduce support and optimizations for platforms like Intel GPUs (XPU), AWS Graviton processors, Inductor performance, etc.Throughout the year the PyTorch Team has been working hard to introduce a number of new PyTorch-native libraries! The ExecuTorch team released their alpha in collaboration with partners from Arm, Apple, and Qualcomm Technologies, Inc. then quickly followed with a beta focused on stability and adding MediaTek. TorchTune established a PyTorch-native library for easily fine-tuning large language models. TorchAO introduced a PyTorch native library that makes models faster and smaller by leveraging low bit dtypes, quantization and sparsity. TorchCodec was launched to give developers a simple, performant, and PyTorch native way to decode videos into tensors. TorchRec 1.0 was released, the first stable release of the PyTorch native recommendation systems library.We’ve also had a number of strong technical showcases throughout the year to highlight how PyTorch can be used! TorchTitan exhibited what an open source, PyTorch-native distributed training system could look like for training large language models (LLMs). TorchChat showcased how to seamlessly and performantly run LLMs across laptop, desktop, and mobile devices.As well we were very excited to include multiple new projects into the PyTorch ecosystem throughout 2024, including the introduction of vLLM into the PyTorch Ecosystem, a state-of-the-art inference engine, which gives machine learning engineers an easy, fast, and cheap way of serving LLMs. If you are interested in joining the PyTorch Ecosystem, please join!'
    test_text_1 = "Mind Your Nose.How smell training can change your brain in six weeks — and why it matters.By Ann-Sophie BarwichWhen it comes to training your brain, your sense of smell is possibly the last thing you’d think could strengthen your neural pathways. Learning a new language or reading more books (and fewer social media posts) — sure. But your nose?That’s because the olfactory system is one of the most plastic systems in your brain. Neuroplasticity describes how the brain flexibly adapts to changes in the environment or when exposed to neural damage. Stimulating the brain strengthens existing neural structures and further adds fuel to the brain’s capacity to remain adaptive, thereby keeping it young. And your smell system is particularly adept at repair and renewal. (Olfactory cells have recently been used in human transplant therapy to treat spinal cord injury, for example.)One reason for the olfactory system’s adaptive responsiveness is that it undergoes adult neurogenesis. Humans grow new olfactory neurons every three to four weeks throughout their entire life, not just during child development. (These sensory neurons sit in the mucous of your nose, where they pick up airborne chemicals and send activity signals straight to the core of the brain.) If it weren’t for this ongoing regeneration of sensory cells in your nose, we would stop detecting smells after our first few colds.Neural plasticity weakens as we grow old — and so does our sense of smell. Olfactory performance decreases around the age of 70 as the regeneration of olfactory neurons slows down. Yet this process of regeneration never stops entirely. Training your nose helps slow down that decline and offers a great way to increase your brain’s plasticity. That said, increasing your sensitivity to odors in the environment does not always sound desirable. Smell usually comes with negative connotations: that whiff of urine in the metro, that overpowering literal skunk, or that trail of body odor from the person walking in front of you. But paying more attention to the smells around you also has benefits, and not just for a greater enjoyment of food aromas and neighbors’ gardens.Recent studies show that olfactory abilities correspond with differences in cortical areas involved in smell processing in the brain. Johannes Frasnelli, an olfactory scientist at the University of Quebec in Trois-Rivi?res, explained: “We did some studies where we saw that there is a link between the structure of certain brain regions-like the thickness of the cortex and the thickness of the gray matter layer in certain brain olfactory processing regions-and the ability to perceive.” Frasnelli and his colleagues found that people with better perceptual capacities had a thicker cortex. When they looked at people who had lost their sense of smell, they also saw a reduction of cortical matter in areas involved in odor processing.That raises the question: Could you change the structure of your brain simply by smelling things? In 2019, Frasnelli’s group discovered that undergoing as little as six weeks of intense olfactory training results in significant structural changes in some regions of the brain (namely, the right inferior frontal gyrus, the bilateral fusiform gyrus, and the right entorhinal cortex).Participants were given three tasks with a cognitive component.The first task was a classification task. Participants had to organize two simple odor mixtures by ordering each from lowest to highest concentration. The second was an identification task. Participants were presented with a target odor blended with a citrus scent in a specific ratio (4%). Then they were given the same blend in different ratios and asked to order them according to quality (more citrusy or less?). Lastly, the detection task: Was the learned target odor present in a range of 14 samples of different odor mixtures or not?This entire exercise was undertaken each day for 20 minutes during the six weeks. Responses were monitored and evaluated on speed and accuracy.Such intense olfactory training led to a general improvement in olfactory performance. Plus, the increase of olfactory skill was not restricted to the training exercises but also transferred to other olfactory abilities-abilities that had not been tested as part of the training. These perceptual tests included: the detection threshold of an odor, accuracy in odor discrimination (same or different?), cued odor identification (which of these four descriptors is correct?), and even free odor identification (identifying an odor without cues!).Increasing insight into what the nose knows, and how it communicates with the brain, has broader implications-even philosophical ones. Old (yet still prevalent) cookie-cutter views of the mind coax us to believe that our senses are passive-indifferently picking up signals in the world that are then processed by the brain. Perception, in such views, is a process separate from cognition. Highly plastic systems such as olfaction present us with a much more intriguing and interwoven picture of the mind: Training your nose’s performance (just like other cognitive capacities) fundamentally shapes what you perceive by rewiring the system.Your senses are far from being impartial transmitters; what you are able to perceive in the world ultimately hinges on the depth of your cognitive engagement with it. In other words, your mind does not emerge apathetically as a product of some remarkable, intricate molecular twists performed by the brain. The mind is enhanced by what you can train your brain to do. Just like strength is a result of muscle training, cognitive training of the senses is the bodybuilding of the brain."
    
    # Тест 1
    # ❗️ Передаем порог в функцию
    tags_1 = predict_text(test_text_1, model, tokenizer, id_to_tag, DEVICE, PREDICTION_THRESHOLD)
    print(f"\nТекст: '{test_text_1}'")
    print(f"Предсказанные теги: {tags_1}")
    
    # Тест 2
    # ❗️ Передаем порог в функцию
    tags_2 = predict_text(test_text_2, model, tokenizer, id_to_tag, DEVICE, PREDICTION_THRESHOLD)
    print(f"\nТекст: '{test_text_2}'")
    print(f"Предсказанные теги: {tags_2}")