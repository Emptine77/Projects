import torch
import torch.nn as nn
import math

class PositionalEncoding(nn.Module):
    def __init__(self, d_model, dropout_p, max_len=5000):
        super(PositionalEncoding, self).__init__()
        self.dropout = nn.Dropout(p=dropout_p)
        
        pe = torch.zeros(max_len, d_model)
        position = torch.arange(0, max_len, dtype=torch.float).unsqueeze(1)
        div_term = torch.exp(torch.arange(0, d_model, 2).float() * (-math.log(10000.0) / d_model))
        pe[:, 0::2] = torch.sin(position * div_term)
        pe[:, 1::2] = torch.cos(position * div_term)
        pe = pe.unsqueeze(0) 
        self.register_buffer('pe', pe)

    def forward(self, x):
        x = x + self.pe[:, :x.size(1), :].requires_grad_(False)
        return self.dropout(x)


class TransformerEmbedding(nn.Module):
    def __init__(self, vocab_size, d_model, dropout_p, max_len, padding_idx=0):
        super(TransformerEmbedding, self).__init__()
        self.token_embed = nn.Embedding(vocab_size, d_model, padding_idx=padding_idx)
        self.pos_embed = PositionalEncoding(d_model, dropout_p, max_len)
        self.d_model = d_model

    def forward(self, input_ids):
        x = self.token_embed(input_ids) * math.sqrt(self.d_model)
        x = self.pos_embed(x)
        return x

class EncoderOnlyClassifier(nn.Module):
    
    def __init__(self, vocab_size, num_tags, d_model, n_layers, n_heads, ffn_hid_dim, dropout_p, max_len):
        super(EncoderOnlyClassifier, self).__init__()
        
        self.embedding = TransformerEmbedding(
            vocab_size=vocab_size, 
            d_model=d_model, 
            dropout_p=dropout_p, 
            max_len=max_len,
            padding_idx=0  
        )

        encoder_layer = nn.TransformerEncoderLayer(
            d_model=d_model,
            nhead=n_heads,
            dim_feedforward=ffn_hid_dim,
            dropout=dropout_p,
            activation='gelu',
            batch_first=True,
            norm_first=True
        )
        self.transformer_encoder = nn.TransformerEncoder(
            encoder_layer=encoder_layer,
            num_layers=n_layers
        )
        self.final_layer_norm = nn.LayerNorm(d_model)
        self.classification_head = nn.Linear(d_model, num_tags)
        
        self._init_weights()

    def _init_weights(self):
        for p in self.parameters():
            if p.dim() > 1:
                nn.init.xavier_uniform_(p)

    def forward(self, input_ids, attention_mask):
        """
        input_ids: (batch_size, seq_len)
        attention_mask: (batch_size, seq_len)
        """
    
        x = self.embedding(input_ids)
        
        padding_mask = (attention_mask == 0)
        x = self.transformer_encoder(x, src_key_padding_mask=padding_mask)
        x = self.final_layer_norm(x)

        input_mask_expanded = attention_mask.unsqueeze(-1).expand(x.size()).float()
        sum_embeddings = torch.sum(x * input_mask_expanded, 1)
        
        sum_mask = input_mask_expanded.sum(1)
        sum_mask = torch.clamp(sum_mask, min=1e-9)
        
        pooled_output = sum_embeddings / sum_mask
        
        logits = self.classification_head(pooled_output)
        
        return logits