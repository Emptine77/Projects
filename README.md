# **Resume Analysis Project**
![Python version](https://img.shields.io/badge/python-3.8%20%7C%203.9%20%7C%203.10-blue?style=flat-square)  
![License](https://img.shields.io/badge/license-MIT-green?style=flat-square)  
![Library](https://img.shields.io/badge/spaCy-3.0+-orange?style=flat-square)  
![Analysis](https://img.shields.io/badge/Data%20Analysis-Pandas%20%7C%20Matplotlib-yellow?style=flat-square)  

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
