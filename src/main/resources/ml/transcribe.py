from transformers import Wav2Vec2Processor, Wav2Vec2ForCTC
import torch
import librosa
import sys

model = Wav2Vec2ForCTC.from_pretrained(r'yongjian/wav2vec2-large-a')  # Note: PyTorch Model
processor = Wav2Vec2Processor.from_pretrained(r'yongjian/wav2vec2-large-a')


def main(*args):
    audio_file, sr = librosa.load(args[0][0])

    sample_rate = processor.feature_extractor.sampling_rate
    with torch.no_grad():
        model_inputs = processor(audio_file, sampling_rate=sample_rate, return_tensors="pt", padding=True)
        logits = model(model_inputs.input_values, attention_mask=model_inputs.attention_mask).logits
        pred_ids = torch.argmax(logits, dim=-1).cpu()
        pred_text = processor.batch_decode(pred_ids)
    print(pred_text[0])


if __name__ == '__main__':
    main(sys.argv[1:])
