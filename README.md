# **Resume Analysis Project**
![Python Version](https://img.shields.io/badge/python-3.10-blue?style=flat-square)  
![License](https://img.shields.io/badge/license-MIT-green?style=flat-square)  
![pandas](https://img.shields.io/badge/pandas-1.4+-blue?style=flat-square&logo=pandas)  
![spaCy](https://img.shields.io/badge/spaCy-3.0+-orange?style=flat-square&logo=spacy)  
![Matplotlib](https://img.shields.io/badge/Matplotlib-3.5+-yellowgreen?style=flat-square&logo=matplotlib)  
![Seaborn](https://img.shields.io/badge/Seaborn-0.11+-brightgreen?style=flat-square)  
![NumPy](https://img.shields.io/badge/NumPy-1.22+-blueviolet?style=flat-square&logo=numpy)

## **Description**  
This Python-based data analysis project processes job market data to identify the most in-demand skills across different job roles, levels, and locations. The system analyzes job postings datasets using NLP techniques and generates visualizations to highlight key skill trends in the employment market.

## **Key Features**  
- Extracts skills from job descriptions using NLP pattern matching  
- Combines structured and unstructured skill data sources  
- Generates 5 types of analytical visualizations  
- Compares skill demands across job levels, types, and countries  
- Identifies top skills for specific job positions  

## **Technologies Used**  
* `Pandas` - Data manipulation and analysis  
* `spaCy` - Natural Language Processing  
* `Matplotlib/Seaborn` - Data visualization  
* `NumPy` - Numerical operations  
* `PhraseMatcher` - Skill extraction from text  

## **Data Sources**  
The analysis uses three primary datasets:  

1. **Job Postings** (`job_postings.csv`)  
   - Contains job metadata (position, level, type, country)  
   - Includes temporal data (`first_seen`, `last_processed_time`)  
   - Size: ~52MB  

2. **Job Skills** (`job_skills.csv`)  
   - Contains structured skill lists  
   - Size: ~6.6MB  

3. **Job Summary** (`job_summary.csv`)  
   - Contains full-text job descriptions  
   - Size: ~3.9MB  

## **Analysis Workflow**  
```mermaid
graph TD
    A[Load Datasets] --> B[Prepare Skill Vocabulary]
    B --> C[Configure NLP Pipeline]
    C --> D[Extract Skills from Descriptions]
    D --> E[Parse Structured Skills]
    E --> F[Combine Skill Sources]
    F --> G[Analyze Overall Skills]
    G --> H[Compare by Job Level]
    H --> I[Job Type Heatmap]
    I --> J[Country Comparison]
    J --> K[Position-specific Analysis]
```
## **Visualizations Generated**
1. **Top-10 Skills (Overall)**
   
<img width="1000" height="600" alt="Figure_1" src="https://github.com/user-attachments/assets/4244f285-4ba9-4661-af86-c94993bc30f9" />

Horizontal bar chart showing most frequent skills
3. **Skills by Job Level**

<img width="1200" height="600" alt="Figure_2" src="https://github.com/user-attachments/assets/c84a3c59-f8d0-45bd-9154-168b3a2037ce" />

Grouped bar chart comparing skills across experience levels
4. **Skills × Job Type Heatmap**

<img width="800" height="600" alt="Figure_3" src="https://github.com/user-attachments/assets/d5d88c96-2e32-4f23-b711-91346f6b70d2" />

Color-coded matrix showing skill frequency by job type
5. **Skills by Country**

<img width="1200" height="600" alt="Figure_4" src="https://github.com/user-attachments/assets/c5fb22a1-6d72-4165-b133-6b8b7c1d2076" />

Grouped bar chart showing top skills in different countries
6. **Skills × Positions Matrix**

<img width="1400" height="1323" alt="Figure_5" src="https://github.com/user-attachments/assets/f2ffc726-d624-4ede-8ebf-bc38c4dc6e40" />

Comprehensive heatmap showing skill relevance for specific roles

## Installation & Usage
### 1. Clone repository
`git clone https://github.com/yourusername/resume-analysis.git`

### 2. Install dependencies
`pip install -r requirements.txt`
pandas==1.4.0
spacy==3.4.0
matplotlib==3.5.0
seaborn==0.11.2
numpy==1.22.0
python-dateutil==2.8.2

### 3. Place data files in project root:
    - job_postings.csv
    - job_skills.csv
    - job_summary.csv
    
### 4. Run analysis
`python resumeParser.py`
## License
This project is licensed under the MIT License - see the LICENSE file for details.
