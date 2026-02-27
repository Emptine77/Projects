import pandas as pd
import ast
import json

def safe_literal_eval(tag_data):
    """
    Безпечно перетворює рядок у список.
    Якщо на вході 'nan' або пошкоджений рядок, поверне порожній список.
    """
    if isinstance(tag_data, str):
        try:
            parsed = ast.literal_eval(tag_data)
            if isinstance(parsed, list):
                return parsed
            else:
                return []
        except (ValueError, SyntaxError):
            return []
    return []

def load_and_clean_data(csv_path, text_col, tag_col, parsed_tag_col):
    """
    Завантажує CSV, очищає NaN у тексті та парсить теги.
    """
    print(f"Завантаження даних з {csv_path}...")
    df = pd.read_csv(
        csv_path,
        encoding='utf-8',
        sep=';',
        encoding_errors='ignore'
    )
    
    print(f"Очищення {text_col} від NaN...")
    df[text_col] = df[text_col].fillna('').astype(str)
    
    print(f"Парсинг тегів з {tag_col}...")
    df[parsed_tag_col] = df[tag_col].apply(safe_literal_eval)
    
    return df

def build_tag_maps(df, parsed_tag_col):
    """
    Будує словники tag_to_id та id_to_tag на основі DataFrame.
    """
    print("Побудова словника тегів...")
    all_tags = df[parsed_tag_col].explode()
    unique_tags = all_tags.dropna().unique()
    
    tag_to_id = {tag: i for i, tag in enumerate(unique_tags)}
    id_to_tag = {i: tag for tag, i in tag_to_id.items()}
    num_tags = len(tag_to_id)
    
    print(f"Знайдено {num_tags} унікальних тегів.")
    return tag_to_id, id_to_tag, num_tags

def save_tag_maps(tag_to_id, id_to_tag, num_tags, filepath, best_threshold=None):
    data = {
        'tag_to_id': tag_to_id,
        'id_to_tag': id_to_tag,
        'num_tags': num_tags,
        'best_threshold': best_threshold # ❗️ Новое поле
    }
    with open(filepath, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=4)
    
    if best_threshold:
        print(f"Словники тегів і поріг ({best_threshold:.4f}) збережені в {filepath}")
    else:
        print(f"Словники тегів збережені в {filepath}")


def load_tag_maps(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        data = json.load(f)
    print(f"Словники тегів завантажені з {filepath}")
    
    best_threshold = data.get('best_threshold', 0.5)
    if best_threshold is None:
        best_threshold = 0.5
        
    print(f"Використовуваний поріг передбачення: {best_threshold}")
    
    id_to_tag_fixed = {int(k): v for k, v in data['id_to_tag'].items()}
    
    return data['tag_to_id'], id_to_tag_fixed, data['num_tags'], best_threshold
    
def filter_rare_tags(df, parsed_col_name, filtered_col_name, min_freq):
    """
    Відсіює рідкісні теги з DataFrame.
    1. Рахує частоту всіх тегів.
    2. Створює список "частих" тегів (>= min_freq).
    3. Створює новий стовпець 'filtered_col_name', 
       що містить лише "часті" теги.
    """
    print(f"Фільтрація рідкісних тегів (поріг: < {min_freq} входжень)...")
    
    all_tags = df[parsed_col_name].explode()
    tag_counts = all_tags.value_counts()
    
    frequent_tags = tag_counts[tag_counts >= min_freq].index
    frequent_tags_set = set(frequent_tags)
    
    original_tag_count = len(tag_counts)
    frequent_tag_count = len(frequent_tags_set)
    
    print(f"Було {original_tag_count} унікальних тегів.")
    print(f"Залишилось {frequent_tag_count} "
          f"унікальних 'частих' тегів (>= {min_freq} входжень).")
    
    def filter_tags(tags_list):
        return [tag for tag in tags_list if tag in frequent_tags_set]
        
    df[filtered_col_name] = df[parsed_col_name].apply(filter_tags)
    
    return df