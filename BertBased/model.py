# model.py
"""
Определение архитектуры нейронной сети.
Использует AutoModel для гибкой загрузки pre-trained моделей.
"""

import torch
import torch.nn as nn
from transformers import AutoModel, AutoConfig

class EncoderOnlyClassifier(nn.Module):
    
    def __init__(self, model_name, num_tags):
        """
        Инициализирует модель.
        
        Args:
            model_name (str): Имя модели из Hugging Face (напр., 'bert-base-multilingual-cased')
            num_tags (int): Количество уникальных тегов для классификации.
        """
        super(EncoderOnlyClassifier, self).__init__()
        
        # 1. Загружаем конфигурацию, чтобы узнать размер скрытого слоя
        config = AutoConfig.from_pretrained(model_name)
        
        # 2. Загружаем саму pre-trained модель (трансформер)
        # Это "тело" модели без головы
        self.transformer = AutoModel.from_pretrained(model_name)
        
        # 3. Наша собственная "голова" для multi-label классификации
        # Мы берем d_model (hidden_size) прямо из конфига загруженной модели
        self.classification_head = nn.Linear(config.hidden_size, num_tags)
        
        # (Опционально) Добавим Dropout для регуляризации головы
        self.dropout = nn.Dropout(0.1)

        # ❗️ Веса инициализировать НЕ НУЖНО. 
        # self.transformer уже обучен. 
        # self.classification_head PyTorch инициализирует сам.

    def forward(self, input_ids, attention_mask, token_type_ids=None):
        """
        Прямой проход.
        
        Args:
            input_ids: (batch_size, seq_len)
            attention_mask: (batch_size, seq_len)
            token_type_ids: (batch_size, seq_len) - опционально, 
                                AutoModel сам разберется, если он не нужен (как в DistilBERT)
        """
        
        # 1. Прогоняем данные через pre-trained трансформер
        # Мы НЕ используем token_type_ids в аргументах, если модель
        # их не поддерживает (например, DistilBERT).
        # Но BERT их использует. Передадим их, если они есть.
        
        transformer_args = {
            'input_ids': input_ids,
            'attention_mask': attention_mask
        }
        # Некоторые модели (как BERT) используют token_type_ids, 
        # некоторые (как DistilBERT) - нет.
        if token_type_ids is not None:
             transformer_args['token_type_ids'] = token_type_ids
             
        outputs = self.transformer(**transformer_args)
        
        # 2. Получаем выход
        # `outputs.last_hidden_state` имеет размер (batch_size, seq_len, hidden_size)
        last_hidden_state = outputs.last_hidden_state

        # 3. Агрегация
        # Мы берем выходные векторы только для ПЕРВОГО токена ([CLS])
        # (batch_size, seq_len, hidden_size) -> (batch_size, hidden_size)
        cls_token_output = last_hidden_state[:, 0, :]
        
        # (Можно было бы использовать `outputs.pooler_output`, но он есть не у всех
        # моделей. Взятие [CLS] токена - более универсальный способ).
        
        cls_token_output = self.dropout(cls_token_output)
        
        # 4. Прогоняем через нашу "голову"
        # (batch_size, hidden_size) -> (batch_size, num_tags)
        logits = self.classification_head(cls_token_output)
        
        return logits