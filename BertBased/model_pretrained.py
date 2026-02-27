import torch.nn as nn
from transformers import AutoModel, AutoConfig

import torch.nn as nn
from transformers import AutoModel, AutoConfig

class EncoderOnlyClassifier(nn.Module):
    
    def __init__(self, model_name, num_tags, freeze_encoder=False, use_gradient_checkpointing=True):
        super(EncoderOnlyClassifier, self).__init__()
        
        config = AutoConfig.from_pretrained(model_name)
        self.transformer = AutoModel.from_pretrained(model_name, config=config)
        
        if use_gradient_checkpointing and self.transformer.supports_gradient_checkpointing:
            self.transformer.gradient_checkpointing_enable()
            print(" Gradient Checkpointing увімкнено!")

        if freeze_encoder:
            for param in self.transformer.parameters():
                param.requires_grad = False
            print(" Ваги трансформера заморожені. Навчається лише голова класифікації.")
            
        self.classification_head = nn.Linear(config.hidden_size, num_tags)
        self.dropout = nn.Dropout(0.1)
        
        self._init_weights()

    def _init_weights(self):
        nn.init.xavier_uniform_(self.classification_head.weight)
        if self.classification_head.bias is not None:
            nn.init.constant_(self.classification_head.bias, 0)

    def forward(self, input_ids, attention_mask, token_type_ids=None):
        transformer_args = {
            'input_ids': input_ids,
            'attention_mask': attention_mask
        }

        if token_type_ids is not None:
             transformer_args['token_type_ids'] = token_type_ids
             
        outputs = self.transformer(**transformer_args)
        
        last_hidden_state = outputs.last_hidden_state
        cls_token_output = last_hidden_state[:, 0, :]
        cls_token_output = self.dropout(cls_token_output)
        
        logits = self.classification_head(cls_token_output)
        
        return logits