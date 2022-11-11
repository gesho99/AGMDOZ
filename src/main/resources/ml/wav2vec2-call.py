# Load the model and processor
from transformers import Wav2Vec2Processor, Wav2Vec2ForCTC
import numpy as np
import torch
import librosa

model = Wav2Vec2ForCTC.from_pretrained(r'yongjian/wav2vec2-large-a') # Note: PyTorch Model
processor = Wav2Vec2Processor.from_pretrained(r'yongjian/wav2vec2-large-a')

# Load input
np_wav = np.random.normal(size=(16000)).clip(-1, 1) # change it to your sample
audiofile, sr = librosa.load('test-audio.mp3')

# Inference
sample_rate = processor.feature_extractor.sampling_rate
with torch.no_grad():
    model_inputs = processor(audiofile, sampling_rate=sample_rate, return_tensors="pt", padding=True)
    logits = model(model_inputs.input_values, attention_mask=model_inputs.attention_mask).logits # use .cuda() for GPU acceleration
    pred_ids = torch.argmax(logits, dim=-1).cpu()
    pred_text = processor.batch_decode(pred_ids)
print('Transcription:', pred_text)