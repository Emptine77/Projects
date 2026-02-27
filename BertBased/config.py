"""Generated Config"""
USE_PRETRAINED_MODEL = True
DATA_FILE = r'dataset.csv'
TAG_MAP_FILE = r'tag_map.json'
BEST_MODEL_PATH = r'from_scrath_10_d03.bin'
TEXT_COLUMN_NAME = 'text'
TAG_COLUMN_NAME = 'tags'
PARSED_TAG_COLUMN = 'parsed_tags_list'
FILTERED_TAG_COLUMN = 'filtered_tags_list'
MIN_TAG_FREQUENCY = 3
MODEL_NAME = 'distilbert-base-multilingual-cased'
MAX_LENGTH = 256
BATCH_SIZE = 8
D_MODEL = 256
DROPOUT_P = 0.3
N_HEADS = 4
N_LAYERS = 2
FFN_HID_DIM = 1024
NUM_EPOCHS = 10
LEARNING_RATE = 5e-05
VALIDATION_SPLIT = 0.1
SEED = 42
POS_WEIGHT = 15.0

# Advanced Memory & Training settings
FREEZE_ENCODER = False
GRADIENT_CHECKPOINTING = True
