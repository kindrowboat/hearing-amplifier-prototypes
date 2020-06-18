package main

import (
	"fmt"
	"time"

	"github.com/gordonklaus/portaudio"
)

func main() {
	portaudio.Initialize()
	defer portaudio.Terminate()
	e := newEcho()
	defer e.Close()
	chk(e.Start())
	time.Sleep(60 * time.Second)
	chk(e.Stop())
}

type echo struct {
	*portaudio.Stream
	buffer []float32
}

func newEcho() *portaudio.Stream {
	h, err := portaudio.DefaultHostApi()
	chk(err)
	p := portaudio.HighLatencyParameters(h.DefaultInputDevice, h.DefaultOutputDevice)
	p.Input.Channels = 1
	p.Output.Channels = 1
	e, err := portaudio.OpenStream(p, processAudio)
	chk(err)
	return e
}

func processAudio(in, out []float32) {
	copy(out, in)
}

func chk(err error) {
	if err != nil {
		fmt.Println(err)
	}
}
