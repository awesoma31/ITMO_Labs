package main

import (
	"flag"
	"fmt"
	// "math"
	"strings"

	"gonum.org/v1/plot"
	"gonum.org/v1/plot/plotter"
	"gonum.org/v1/plot/plotutil"
	"gonum.org/v1/plot/vg"
	"gonum.org/v1/plot/vg/draw"
	"gonum.org/v1/plot/vg/vgimg"
	"os"
)

/* ---------- параметры ---------- */

const (
	grid  = 30
	alpha = 0.10
	beta  = 0.08
)

/* ---------- структуры ---------- */

type Soldier struct {
	x, y float64
	rank float64
}

/* ---------- геометрия ---------- */

func insideTri(x, y float64) bool {
	return y >= 0 && y <= 1 && x >= 0.5-y*0.5 && x <= 0.5+y*0.5
}

/* ---------- генерация ---------- */

func createSoldiers() []Soldier {
	s := make([]Soldier, 0, grid*grid/2)
	for j := 0; j <= grid; j++ {
		y := 1 - float64(j)/float64(grid)
		rank := float64(j) / float64(grid)
		for i := 0; i <= grid; i++ {
			x := float64(i) / float64(grid)
			if insideTri(x, y) {
				s = append(s, Soldier{x, y, rank})
			}
		}
	}
	return s
}

/* ---------- перемещение ---------- */

func move(src []Soldier, dir string) []Soldier {
	dst := make([]Soldier, len(src))
	left, down := false, false
	switch strings.ToLower(dir) {
	case "down":
		down = true
	case "left":
		left = true
	case "downleft", "leftdown", "":
		left, down = true, true
	default:
		fmt.Printf("unknown dir=%s, defaulting to downleft\n", dir)
		left, down = true, true
	}
	for i, p := range src {
		dx, dy := 0.0, 0.0
		if left {
			dx = -beta * p.rank
		}
		if down {
			dy = -alpha * p.rank
		}
		dst[i] = Soldier{p.x + dx, p.y + dy, p.rank}
	}
	return dst
}

/* ---------- визуализация ---------- */

func makePlot(points []Soldier, title string, color int) *plot.Plot {
	p := plot.New()
	p.Title.Text = title
	p.X.Label.Text = "x"
	p.Y.Label.Text = "y"
	p.X.Min, p.X.Max = -0.1, 1.1
	p.Y.Min, p.Y.Max = -0.1, 1.1

	data := make(plotter.XYs, len(points))
	for i, pt := range points {
		data[i].X = pt.x
		data[i].Y = pt.y
	}
	s, _ := plotter.NewScatter(data)
	s.GlyphStyle.Color = plotutil.Color(color)
	s.GlyphStyle.Radius = vg.Points(1)
	p.Add(s)

	return p
}

func drawSideBySide(before, after *plot.Plot, filename string) error {
	const cols = 2
	const width = 6 * vg.Inch
	const height = 3.5 * vg.Inch

	img := vgimg.New(width, height)
	dc := draw.New(img)

	tiles := draw.Tiles{
		Rows: 1,
		Cols: cols,
	}
	canvases := plot.Align([][]*plot.Plot{{before, after}}, tiles, dc)

	before.Draw(canvases[0][0])
	after.Draw(canvases[0][1])

	// 💾 Сохраняем в PNG-файл
	w := vgimg.PngCanvas{Canvas: img}
	f, err := os.Create(filename)
	if err != nil {
		return err
	}
	defer f.Close()

	_, err = w.WriteTo(f)
	return err
}

/* ---------- main ---------- */

func main() {
	dir := flag.String("dir", "downleft", "step direction: down | left | downleft")
	flag.Parse()

	src := createSoldiers()
	dst := move(src, *dir)

	plot1 := makePlot(src, "До", 0)
	plot2 := makePlot(dst, "После ("+*dir+")", 1)

	if err := drawSideBySide(plot1, plot2, "out.png"); err != nil {
		panic(err)
	}
}
