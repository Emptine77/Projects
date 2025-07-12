Resume Analysis Project
https://img.shields.io/badge/python-3.8%2520%257C%25203.9%2520%257C%25203.10-blue?style=flat-square  
https://img.shields.io/badge/license-MIT-green?style=flat-square  
https://img.shields.io/badge/spaCy-3.0+-orange?style=flat-square  
https://img.shields.io/badge/Data%2520Analysis-Pandas%2520%257C%2520Matplotlib-yellow?style=flat-square

Description
This Python-based data analysis project processes job market data to identify the most in-demand skills across different job roles, levels, and locations. The system analyzes job postings datasets using NLP techniques and generates visualizations to highlight key skill trends in the employment market. Developed as a practical application of data science techniques for job market analysis.

Key Features
Extracts skills from job descriptions using NLP pattern matching

Combines structured and unstructured skill data sources

Generates 5 types of analytical visualizations

Compares skill demands across job levels, types, and countries

Identifies top skills for specific job positions

Technologies Used
Pandas - Data manipulation and analysis

spaCy - Natural Language Processing

Matplotlib/Seaborn - Data visualization

NumPy - Numerical operations

PhraseMatcher - Skill extraction from text

Data Sources
The analysis uses three primary datasets:

Job Postings (job_postings.csv)

Contains job metadata (position, level, type, country)

Includes temporal data (first_seen, last_processed_time)

Size: ~52MB

Job Skills (job_skills.csv)

Contains structured skill lists

Size: ~6.6MB

Job Summary (job_summary.csv)

Contains full-text job descriptions

Size: ~3.9MB

Analysis Workflow
The system follows a 10-step analytical pipeline:

Data Loading: Reads and merges all datasets

Skill Preparation: Defines target skill vocabulary

NLP Setup: Configures spaCy and PhraseMatcher

Summary Extraction: Parses skills from job descriptions

Structured Skill Parsing: Processes comma-separated skills

Data Combination: Merges skills from all sources

Top Skills Identification: Calculates overall skill frequency

Level-based Analysis: Compares skills by job seniority

Job Type Heatmap: Visualizes skills vs employment type

Geographical Comparison: Analyzes skills by country

Position-specific Analysis: Examines skills for top roles

Visualizations Generated
Top-10 Skills (Overall)
https://via.placeholder.com/400x250/CCCCCC/333333?text=Top+Skills+Chart
Horizontal bar chart showing most frequent skills

Skills by Job Level
https://via.placeholder.com/500x300/CCCCCC/333333?text=Skills+by+Level
Grouped bar chart comparing skills across experience levels

Skills × Job Type Heatmap
https://via.placeholder.com/400x300/CCCCCC/333333?text=Skills+Heatmap
Color-coded matrix showing skill frequency by job type

Skills by Country
https://via.placeholder.com/500x300/CCCCCC/333333?text=Skills+by+Country
Grouped bar chart showing top skills in different countries

Skills × Positions Matrix
https://via.placeholder.com/600x400/CCCCCC/333333?text=Skills+vs+Positions
Comprehensive heatmap showing skill relevance for specific roles

Installation & Usage
bash
# Clone repository
git clone https://github.com/yourusername/resume-analysis.git

# Install dependencies
pip install -r requirements.txt

# Run analysis
python resumeParser.py
Dependencies (see requirements.txt):

text
pandas==1.4.0
spacy==3.4.0
matplotlib==3.5.0
seaborn==0.11.2
numpy==1.22.0
python-dateutil==2.8.2
Sample Output
The script generates:

5 interactive visualizations in separate windows

Processed dataset with extracted skills (all_skills column)

Terminal output showing analysis progress

Customization
Modify these variables for different analyses:

python
# Expand skill vocabulary
skills_list = [
    "python", "r", "sql", "machine learning", 
    # Add new skills here...
]

# Adjust top-N parameters
top_skills_count = 15
top_countries_count = 5
top_positions_count = 10
License
This project is licensed under the MIT License - see the LICENSE file for details.
