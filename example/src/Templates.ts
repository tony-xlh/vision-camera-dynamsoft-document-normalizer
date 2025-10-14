export const blackWhite = `{
  "CaptureVisionTemplates": [
    {
      "Name": "DetectDocumentBoundaries_Default",
      "ImageROIProcessingNameArray": [
        "roi-detect-document-boundaries"
      ],
      "MaxParallelTasks": 0
    },
    {
      "Name": "DetectAndNormalizeDocument_Default",
      "ImageROIProcessingNameArray": [
        "roi-detect-and-normalize-document"
      ],
      "MaxParallelTasks": 0
    },
    {
      "Name": "NormalizeDocument_Default",
      "ImageROIProcessingNameArray": [
        "roi-normalize-document"
      ],
      "MaxParallelTasks": 0
    }
  ],
  "TargetROIDefOptions": [
    {
      "Name": "roi-detect-document-boundaries",
      "TaskSettingNameArray": [
        "task-detect-document-boundaries"
      ]
    },
    {
      "Name": "roi-detect-and-normalize-document",
      "TaskSettingNameArray": [
        "task-detect-and-normalize-document"
      ]
    },
    {
      "Name": "roi-normalize-document",
      "TaskSettingNameArray": [
        "task-normalize-document"
      ]
    }
  ],
  "DocumentNormalizerTaskSettingOptions": [
    {
      "Name": "task-detect-and-normalize-document",
      "MaxThreadsInOneTask": 1,
      "SectionArray": [
        {
          "Section": "ST_DOCUMENT_DETECTION",
          "ImageParameterName": "ip-detect-and-normalize"
        },
        {
          "Section": "ST_DOCUMENT_DESKEWING",
          "ImageParameterName": "ip-detect-and-normalize"
        },
        {
          "Section": "ST_IMAGE_ENHANCEMENT",
          "ImageParameterName": "ip-detect-and-normalize",
          "StageArray": [
            {
              "Stage": "SST_ENHANCE_IMAGE",
              "ColourMode": "ICM_BINARY"
            }
          ]
        }
      ]
    },
    {
      "Name": "task-detect-document-boundaries",
      "MaxThreadsInOneTask": 1,
      "SectionArray": [
        {
          "Section": "ST_DOCUMENT_DETECTION",
          "ImageParameterName": "ip-detect"
        }
      ]
    },
    {
      "Name": "task-normalize-document",
      "MaxThreadsInOneTask": 1,
      "SectionArray": [
        {
          "Section": "ST_DOCUMENT_DESKEWING",
          "ImageParameterName": "ip-normalize"
        },
        {
          "Section": "ST_IMAGE_ENHANCEMENT",
          "ImageParameterName": "ip-normalize",
          "StageArray": [
            {
              "Stage": "SST_ENHANCE_IMAGE",
              "ColourMode": "ICM_BINARY"
            }
          ]
        }
      ]
    }
  ],
  "ImageParameterOptions": [
    {
      "Name": "ip-detect-and-normalize",
      "ApplicableStages": [
        {
          "Stage": "SST_DETECT_TEXT_ZONES",
          "TextDetectionMode": {
            "Mode": "TTDM_WORD",
            "Direction": "HORIZONTAL",
            "Sensitivity": 7
          }
        },
        {
          "Stage": "SST_BINARIZE_IMAGE",
          "BinarizationModes": [
            {
              "Mode": "BM_LOCAL_BLOCK",
              "BlockSizeX": 25,
              "BlockSizeY": 25,
              "EnableFillBinaryVacancy": 0,
              "ThresholdCompensation": 5
            }
          ]
        },
        {
          "Stage": "SST_CONVERT_TO_GRAYSCALE",
          "ColourConversionModes": [
            {
              "Mode": "CICM_GENERAL"
            },
            {
              "Mode": "CICM_EDGE_ENHANCEMENT"
            },
            {
              "Mode": "CICM_HSV",
              "ReferChannel": "H_CHANNEL"
            }
          ]
        },
        {
          "Stage": "SST_DETECT_TEXTURE",
          "TextureDetectionModes": [
            {
              "Mode": "TDM_GENERAL_WIDTH_CONCENTRATION",
              "Sensitivity": 8
            }
          ]
        }
      ]
    },
    {
      "Name": "ip-detect",
      "ApplicableStages": [
        {
          "Stage": "SST_DETECT_TEXT_ZONES",
          "TextDetectionMode": {
            "Mode": "TTDM_WORD",
            "Direction": "HORIZONTAL",
            "Sensitivity": 7
          }
        },
        {
          "Stage": "SST_BINARIZE_IMAGE",
          "BinarizationModes": [
            {
              "Mode": "BM_LOCAL_BLOCK",
              "BlockSizeX": 25,
              "BlockSizeY": 25,
              "EnableFillBinaryVacancy": 0,
              "ThresholdCompensation": 5
            }
          ]
        },
        {
          "Stage": "SST_CONVERT_TO_GRAYSCALE",
          "ColourConversionModes": [
            {
              "Mode": "CICM_GENERAL"
            },
            {
              "Mode": "CICM_EDGE_ENHANCEMENT"
            },
            {
              "Mode": "CICM_HSV",
              "ReferChannel": "H_CHANNEL"
            }
          ]
        },
        {
          "Stage": "SST_DETECT_TEXTURE",
          "TextureDetectionModes": [
            {
              "Mode": "TDM_GENERAL_WIDTH_CONCENTRATION",
              "Sensitivity": 8
            }
          ]
        }
      ]
    },
    {
      "Name": "ip-normalize",
      "ApplicableStages": [
        {
          "Stage": "SST_DETECT_TEXT_ZONES",
          "TextDetectionMode": {
            "Mode": "TTDM_WORD",
            "Direction": "HORIZONTAL",
            "Sensitivity": 7
          }
        },
        {
          "Stage": "SST_BINARIZE_IMAGE",
          "BinarizationModes": [
            {
              "Mode": "BM_LOCAL_BLOCK",
              "BlockSizeX": 0,
              "BlockSizeY": 0,
              "EnableFillBinaryVacancy": 0
            }
          ]
        }
      ]
    }
  ]
}`
