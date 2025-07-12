import pandas as pd
import spacy
from spacy.matcher import PhraseMatcher
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np

# -----------------------------
# 1. Load and merge data
# -----------------------------
df_post = pd.read_csv('job_postings.csv', parse_dates=['first_seen', 'last_processed_time'])
df_skills = pd.read_csv('job_skills.csv')
df_post['country'] = df_post['search_country']
df = df_post.merge(df_skills, on='job_link', how='inner')

# -----------------------------
# 2. Prepare skill list and PhraseMatcher
# -----------------------------
skills_list = [
    "python", "r", "sql", "machine learning", "deep learning", "nlp",
    "data visualization", "big data", "cloud computing", "statistics",
    "data mining", "data analysis", "data wrangling", "tensorflow",
    "pytorch", "scikit-learn", "pandas", "numpy", "matplotlib", "seaborn",
    "tableau", "power bi", "excel", "aws", "azure", "google cloud",
    "hadoop", "spark", "docker", "kubernetes", "git", "linux"
]
nlp = spacy.blank("en")
matcher = PhraseMatcher(nlp.vocab, attr="LOWER")
patterns = [nlp.make_doc(skill) for skill in skills_list]
matcher.add("SKILL", patterns)

# -----------------------------
# 3. Extract skills from job_summary
# -----------------------------
def extract_summary_skills(text):
    if not isinstance(text, str):
        return []
    doc = nlp.make_doc(text)
    matches = matcher(doc)
    return list({doc[start:end].text for _, start, end in matches})

df['summary_skills'] = df['got_summary'].apply(extract_summary_skills)

# -----------------------------
# 4. Parse skills from job_skills field
# -----------------------------
def parse_skills_field(skills_str):
    if not isinstance(skills_str, str):
        return []
    return [skill.strip().lower() for skill in skills_str.split(',') if skill.strip()]

df['listed_skills'] = df['job_skills'].apply(parse_skills_field)

# -----------------------------
# 5. Combine and prepare for analysis
# -----------------------------
df['all_skills'] = df.apply(lambda row: row['summary_skills'] + row['listed_skills'], axis=1)
df_exploded = df.explode('all_skills').rename(columns={'all_skills': 'skill'})
df_exploded = df_exploded[df_exploded['skill'].notna()]

# -----------------------------
# 6. Overall top‑10 skills
# -----------------------------
top_overall = (
    df_exploded['skill']
    .value_counts()
    .nlargest(10)
    .reset_index()
    .rename(columns={'index': 'skill', 0: 'count'})
)

plt.figure(figsize=(10, 6))
sns.barplot(data=top_overall, x='count', y='skill')
plt.title('Top-10 Most Frequent Skills (Overall)')
plt.xlabel('Frequency')
plt.ylabel('Skill')
plt.tight_layout()
plt.show()

# -----------------------------
# 7. Top skills by job level
# -----------------------------
level_skill_counts = (
    df_exploded
    .groupby(['job_level', 'skill'])
    .size()
    .reset_index(name='count')
)

top7_by_level = (
    level_skill_counts
    .groupby('job_level', group_keys=False)
    .apply(lambda d: d.nlargest(7, 'count'))
    .reset_index(drop=True)
)

pivot_df = (
    top7_by_level
    .pivot(index='skill', columns='job_level', values='count')
    .fillna(0)
)

fig, ax = plt.subplots(figsize=(12, 6))

pivot_df.plot(
    kind='bar',
    width=0.8,
    edgecolor='black',
    ax=ax
)

ax.set_title('Top-7 Skills by Job Level')
ax.set_xlabel('Skill')
ax.set_ylabel('Frequency')
ax.tick_params(axis='x', rotation=45)
ax.legend(title='Job Level')

plt.tight_layout()
plt.show()

# -----------------------------
# 8. Heatmap: Skills × Job Type
# -----------------------------
top15 = top_overall['skill'].tolist()
heat_data = (
    df_exploded[df_exploded['skill'].isin(top15)]
    .pivot_table(index='skill', columns='job_type', aggfunc='size', fill_value=0)
)
plt.figure(figsize=(8, 6))
sns.heatmap(heat_data, annot=True, fmt='d', cmap='Blues')
plt.title('Top-15 Skills by Job Type')
plt.xlabel('Job Type')
plt.ylabel('Skill')
plt.tight_layout()
plt.show()

# -----------------------------
# 9. Skill comparison by country
# -----------------------------
top_countries = df['country'].value_counts().nlargest(5).index.tolist()

country_top_skills = (
    df_exploded[df_exploded['country'].isin(top_countries)]
    .groupby(['country', 'skill'])
    .size()
    .reset_index(name='count')
)

skills_set = (
    country_top_skills
    .groupby('country')
    .apply(lambda d: d.nlargest(5, 'count')['skill'].tolist())
    .explode()
    .unique()
)

plot_data = country_top_skills[
    country_top_skills['country'].isin(top_countries) &
    country_top_skills['skill'].isin(skills_set)
]

plt.figure(figsize=(12, 6))
sns.barplot(
    data=plot_data,
    x='skill',
    y='count',
    hue='country'
)
plt.title('Top Skills by Country')
plt.xlabel('Skill')
plt.ylabel('Frequency')
plt.xticks(rotation=45, ha='right')
plt.legend(title='Country')
plt.tight_layout()
plt.show()

# -----------------------------
# 10. Skill comparison by top job positions
# -----------------------------
top_positions = (
    df_exploded['search_position']
    .value_counts()
    .nlargest(10)
    .index
    .tolist()
)

pos_skill_counts = (
    df_exploded[df_exploded['search_position'].isin(top_positions)]
    .groupby(['search_position', 'skill'])
    .size()
    .reset_index(name='count')
)

skills_set = (
    pos_skill_counts
    .groupby('search_position')
    .apply(lambda df: df.nlargest(10, 'count')['skill'])
    .explode()
    .unique()
)

plot_data = pos_skill_counts[
    pos_skill_counts['skill'].isin(skills_set) &
    pos_skill_counts['search_position'].isin(top_positions)
]

pivot = plot_data.pivot(
    index='skill',
    columns='search_position',
    values='count'
).fillna(0)

plt.figure(figsize=(14, len(skills_set) * 0.4 + 2))
sns.heatmap(
    pivot,
    annot=True,
    fmt='g',
    cmap='YlGnBu',
    cbar_kws={'label': 'Frequency'}
)
plt.title('Top-10 Skills by Top-10 Job Positions')
plt.xlabel('Job Position (search_position)')
plt.ylabel('Skill')
plt.xticks(rotation=45, ha='right')
plt.yticks(rotation=0)
plt.tight_layout()
plt.show()

