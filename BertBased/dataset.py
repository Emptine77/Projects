import torch
from torch.utils.data import Dataset

class TagDataset(Dataset):
    def __init__(self, dataframe, tag_map, num_all_tags, text_col, tag_list_col):
        # Токенізатор і max_len тут більше не потрібні!
        self.texts = dataframe[text_col].tolist() 
        self.tag_lists = dataframe[tag_list_col].tolist()
        self.tag_to_id = tag_map
        self.num_tags = num_all_tags
        
    def __len__(self):
        return len(self.texts)
    
    def __getitem__(self, index):
        # Просто віддаємо сирий текст та мітки
        text = str(self.texts[index])
        tags_list = self.tag_lists[index] 
        
        label_tensor = torch.zeros(self.num_tags, dtype=torch.float)
        for tag in tags_list:
            if tag in self.tag_to_id:
                label_tensor[self.tag_to_id[tag]] = 1
                
        return {
            'text': text,
            'labels': label_tensor
        }

class TagCollator:
    def __init__(self, tokenizer, max_len):
        self.tokenizer = tokenizer
        self.max_len = max_len

    def __call__(self, batch):
        texts = [item['text'] for item in batch]
        labels = [item['labels'] for item in batch]

        encoded_batch = self.tokenizer(
            texts,
            padding=True,
            truncation=True,
            max_length=self.max_len,
            return_tensors="pt"
        )
        
        encoded_batch['labels'] = torch.stack(labels)
        
        return encoded_batch