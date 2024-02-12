import sys
import os
from PIL import Image
from os import walk

template = Image.open("full.png")

def mergeColors(t, m):
    r = (t[0] / 255.0) * (m[0] / 255.0)
    g = (t[1] / 255.0) * (m[1] / 255.0)
    b = (t[2] / 255.0) * (m[2] / 255.0)
    a = (t[3] / 255.0) * (m[3] / 255.0)

    return (int(r * 255), int(g * 255), int(b * 255), int(a * 255))

def mergeWithTemplate(im):
    merged = Image.new("RGBA", template.size)

    for y in range(template.size[1]):
        for x in range(template.size[0]):
            merged.putpixel((x, y), mergeColors(template.getpixel((x, y)), im.getpixel((x, y))))
    return merged

print(f"Loaded template")

indir = sys.argv[1]
outdir = sys.argv[2]
os.makedirs(outdir, 0o777, True)

print(f"Masking template with files from: {indir} ->")

filenames = next(walk(indir), (None, None, []))[2]
print(filenames)

for infile in filenames:
    outfile = f"{outdir}{infile}"
    print(f"Output {outfile}")
    if infile != outfile:
        try:
            with Image.open(indir + infile) as im:
                print(f"Opened {infile} :: {im.size}")
                if im.size != template.size:
                    print("Err: Size doesn't match template")
                    continue
                mergeWithTemplate(im).save(outfile)
                print(f"Saved")
        except OSError:
            print("Err: Failed to process", infile)
