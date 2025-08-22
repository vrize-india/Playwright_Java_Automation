# Tesseract OCR Setup Instructions

## For macOS (your current system):

### 1. Install Tesseract using Homebrew:
```bash
# Install Homebrew if not already installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Tesseract
brew install tesseract

# Install additional language data (optional)
brew install tesseract-lang
```

### 2. Download Tesseract Data Files:
```bash
# Create tessdata directory
mkdir -p src/main/resources/tessdata

# Download English language data
cd src/main/resources/tessdata
wget https://github.com/tesseract-ocr/tessdata/raw/main/eng.traineddata

curl -L -o eng.traineddata https://github.com/tesseract-ocr/tessdata/raw/main/eng.traineddata

ls -la src/main/resources/tessdata/

tesseract --version

# Or download manually from: https://github.com/tesseract-ocr/tessdata
```

### 3. Set Environment Variables (if needed):
```bash
# Add to your ~/.zshrc or ~/.bash_profile
export TESSDATA_PREFIX=/opt/homebrew/share/tessdata
```

## For Windows:

### 1. Download and Install:
- Download from: https://github.com/UB-Mannheim/tesseract/wiki
- Install the executable
- Add to PATH: `C:\Program Files\Tesseract-OCR`

### 2. Download Language Data:
- Download `eng.traineddata` from: https://github.com/tesseract-ocr/tessdata
- Place in: `C:\Program Files\Tesseract-OCR\tessdata\`

## For Linux (Ubuntu/Debian):

```bash
sudo apt update
sudo apt install tesseract-ocr
sudo apt install libtesseract-dev

# Download language data
sudo apt install tesseract-ocr-eng
```

## Alternative: Use System Tesseract Path

If you install Tesseract system-wide, update the datapath in BasePage.java:

```java
// For macOS Homebrew installation
tesseract.setDatapath("/opt/homebrew/share/tessdata");

// For Windows
tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");

// For Linux
tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
```