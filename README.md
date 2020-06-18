# hearing-amplifier-prototypes
Hacked together hearing amplifiers in Kotlin (for Android), golang, and C

The repo has three different projects for looping back audio from microphone to a headphone.

## portaudio\_c

Uses the [portaudio](http://www.portaudio.com/) C library to loopback a mic into headphones. This by far has the least latency, to the point that it's not even noticeable. The pre-compile binary (Linux x64) is available at `portaudio_c/bin/mic_loopback`. 

### Prerequisites

You need PortAudio installed (with the development files if you want to compile your own.) Install on Linux with:

```
sudo apt-get install portaudio19-dev
```

### Gotchas

This tries to grab your default mic and speaker. If that isn't working, you may have to set the device in `mic_loopback.c`. You can use the `sound_devices` binary to see the available devices.

Ubuntu by default uses PulseAudio which emulates ALSA. As a result, you may experience "buffer underrun" which causes the loopback to be a little staticy and choppy. This could be fixed possibly by tuning the app or by removing PulseAudio and using ALSA or JACK directly.

### Compile 
On Ubuntu:

```bash
cd portaudio_c
gcc mic_loopback.c libportaudio.a  -lrt -lm -lasound -ljack -pthread -o bin/mic_loopback
```

## portaudio\_go

Uses a [golang wrapper around portaudio](https://github.com/gordonklaus/portaudio). The latency is noticeable worse than the C version (but not nearly as bad as the Android version). A pre-compiled binary (for Linux x64) is located at `portaudio_go/bin/mic_loopback`

### Prerequisites

*See portaudio\_c prerequisits.*

### Gotchas

For some reason the this has trouble grabbing the sound devices sometimes. If you close down apps that my be using the devices (e.g. a web browser) and rerun the binary (a few times,) eventually it will (should?) eventually work.

### Compile
```bash
cd portaudio_go
go build mic_loopback.go
```

## AndriodSimpleAmplifier

This Android app uses the Android SDK's AudioRecord and AudioTrack interfaces to loop sound back from the microphone to the headphones. Unfortunately, this has substantial latency (~1s). A quick survey of other apps in the Google Play store that do similar things showed that some app have optimized for minimal latency, but still suffer a noticeable delay (~0.5s)

### Prerequisites

The easiest way to build and install this on your device is through [AndroidStudio](https://developer.android.com/studio), provided for free by Google.

### Gotchas

This will not work on the emulated Android devices provided by AndroidStudio. They must be installed on a physical device.

### Compile

[Compile and install this app in AndroidStudio.](https://developer.android.com/studio/run/device)
