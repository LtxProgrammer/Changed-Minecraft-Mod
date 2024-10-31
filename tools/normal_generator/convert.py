import sys
import os
import os.path
import numpy as np
import json
import base64
import io
import math
import copy
import colorama
import uuid
from colorama import Fore, Style
import matplotlib.image as mpimg

def getAllFaces(bbProject, ratio):
    uvs = []
    for elem in bbProject['elements']:
        for face in elem['faces'].values():
            uv = face['uv']
            x0 = int(math.floor(min(uv[0], uv[2]) * ratio[0]))
            x1 = int(math.ceil(max(uv[0], uv[2]) * ratio[0]))
            y0 = int(math.floor(min(uv[1], uv[3]) * ratio[1]))
            y1 = int(math.ceil(max(uv[1], uv[3]) * ratio[1]))
            uvs.append(((x0, x1), (y0, y1)))
    return uvs
            

def normalizeRGB(vec):
    length = np.sqrt(vec[:,:,0]**2 + vec[:,:,1]**2 + vec[:,:,2]**2)
    vec[:,:,0] = vec[:,:,0] / length
    vec[:,:,1] = vec[:,:,1] / length
    vec[:,:,2] = vec[:,:,2] / length
    return vec

def computeNormalMap(heightMap, scale):
    # Code from https://gist.github.com/Huud/63bacf5b8fe9b7b205ee42a786f922f0

    paddedMap = np.zeros((heightMap.shape[0]+2, heightMap.shape[1]+2))
    paddedMap[1:-1,1:-1] = heightMap
    
    paddedMap[0,1:-1] = heightMap[0,:]
    paddedMap[-1,1:-1] = heightMap[-1,:]
    paddedMap[1:-1,0] = heightMap[:,0]
    paddedMap[1:-1,-1] = heightMap[:,-1]
    if heightMap.shape[0] > 1:
        paddedMap[0,1:-1] -= heightMap[1,:] - heightMap[0,:]
        paddedMap[-1,1:-1] -= heightMap[-2,:] - heightMap[-1,:]
    if heightMap.shape[1] > 1:
        paddedMap[1:-1,0] -= heightMap[:,1] - heightMap[:,0]
        paddedMap[1:-1,-1] -= heightMap[:,-2] - heightMap[:,-1]

    normalMap  = np.zeros((paddedMap.shape[0], paddedMap.shape[1], 3))
    tan = np.zeros((paddedMap.shape[0], paddedMap.shape[1], 3))
    bitan   = np.zeros((paddedMap.shape[0], paddedMap.shape[1], 3))

    # we get the normal of a pixel by the 4 pixels around it, so define the top, bottom, left and right pixels arrays,
    # which are just the input image shifted one pixel to the corrosponding direction. We do this by padding the image
    # and then 'cropping' the unneeded sides

    B = np.pad(paddedMap,1,mode='edge')[2:,1:-1]
    T = np.pad(paddedMap,1,mode='edge')[:-2,1:-1]
    L = np.pad(paddedMap,1,mode='edge')[1:-1,0:-2]
    R = np.pad(paddedMap,1,mode='edge')[1:-1,2:]

    #get the tangents of the surface, the normal is their cross product
    tan[:,:,0],tan[:,:,1]       = np.asarray([scale, 0])
    tan[:,:,2] = R-L
    bitan[:,:,0],bitan[:,:,1] = np.asarray([0, scale])
    bitan[:,:,2] = B-T
    normalMap = np.cross(tan,bitan)

    # normalize the normals to get their length to 1, they are called normals after all
    normalMap = normalizeRGB(normalMap)

    # calculations were done for the channels to be in range -1 to 1 for the channels, however the image saving function
    # expects the range 0-1, so just divide these channels by 2 and add 0.5 to be in that range
    normalMap[:,:,0] = (normalMap[:,:,0]/2)+.5
    normalMap[:,:,1] = (normalMap[:,:,1]/2)+.5
    normalMap[:,:,2] = (normalMap[:,:,2]/2)+.5

    # normalizing does most of the job, but clip the remainder just in case 
    return np.clip(normalMap,0.0,1.0)[1:-1,1:-1,:]

def blobToImage(blob):
    blob = blob[blob.find("data:image/png;base64,")+len("data:image/png;base64,"):]
    blob = base64.b64decode(blob)
    buf = io.BytesIO(blob)
    img = mpimg.imread(buf)
    return img

def imageToBlob(img):
    with io.BytesIO() as buf:
        mpimg.imsave(buf, arr=img, format="PNG")
        blob = base64.b64encode(buf.getvalue()).decode("utf-8")
        blob = "data:image/png;base64," + blob
        return blob

def prepNameForNormal(heightMapName):
    if heightMapName.endswith("_h.png") or heightMapName.endswith("_h"):
        return heightMapName.replace("_h", "_n")
    elif heightMapName.endswith(".png"):
        return heightMapName.replace(".png", "_n.png")
    else:
        return heightMapName + "_n"

def printUsage():
    print("CLI tool to convert heightmaps in a BlockBench project into a normal map guided by project geometry.")
    print("Resulting normal map is compatible with LabPBR.")
    print("Usage:\n\tconvert bbProject scale [auto] [save outDir]\n")
    print("\tbbProject  - BlockBench project with textures saved within")
    print("\tscale      - Strength of the normals (0 - inf; smaller values are stronger)")
    print("\tauto       - Indicates the script should pick which choose and save with no user input")
    print("\tsave       - Indicates the script should save the result to a file instead of bbProject\n")
    print("Examples:")
    print("\tconvert project.bbmodel 0.5              : Will prompt to select which textures to read and write")
    print("\tconvert project.bbmodel 0.5 auto         : Will deduce automatically which textures to read and write")
    print("\tconvert project.bbmodel 0.5 save out.png : Will prompt to select to read, then save to file out.png")

def inputFor(prompt, options, default=None):
    while True:
        userInput = input(prompt)
        if userInput in options:
            return userInput
        elif not userInput and default:
            return default
        else:
            print(f"Unexpected value: {userInput}")

if len(sys.argv) < 3:
    printUsage()
    sys.exit()

bbProjectPath = sys.argv[1]
scale = float(sys.argv[2])

normalMapPath = None

auto = False
index = 3
while len(sys.argv) >= index + 1:
    if sys.argv[index] == "auto":
        auto = True
    elif sys.argv[index] == "save":
        index += 1
        if len(sys.argv) < index + 1:
            print(f"{Fore.RED}FATAL: Missing output path{Style.RESET_ALL}")
            sys.exit()
        normalMapPath = sys.argv[index]
    index += 1

with open(bbProjectPath, 'r') as file:
    bbProject = json.load(file)

print(f"Loaded BlockBench project {bbProject['name']}")

selectedTexture = 0
allTextures = bbProject['textures']
if (len(allTextures) == 0):
    print(f"{Fore.RED}FATAL: Project has no textures.{Style.RESET_ALL}")
    sys.exit()
else:
    if not auto:
        print(f"Select heightmap, or press Ctrl+C to exit:")
        for tx in allTextures:
            print(f"\t{selectedTexture}: {tx['name']} [{tx['width']}x{tx['height']}]")
            selectedTexture += 1
        selectedTexture = int(input("heightmap> "))
    else:
        for tx in allTextures:
            if tx['name'].endswith("_h.png") or tx['name'].endswith("_h") or tx['name'].find("height") != -1:
                break
            selectedTexture += 1
        if selectedTexture >= len(allTextures):
            print(f"{Fore.RED}FATAL: Cannot deduce heightmap{Style.RESET_ALL}")
            sys.exit()

heightMapTexture = allTextures[selectedTexture]
print(f"Selected texture {heightMapTexture['name']}")

heightMap = blobToImage(heightMapTexture['source'])
heightMap = heightMap[:,:,0]
print(f"Loaded heightmap {heightMap.shape[0]}x{heightMap.shape[1]}")

normalMap = np.zeros((heightMap.shape[0], heightMap.shape[1], 3))
normalMap[:,:,:] = (0.5, 0.5, 1.0)

resolution = ( bbProject['resolution']['width'], bbProject['resolution']['height'] )
ratio = ( heightMap.shape[0] / resolution[0], heightMap.shape[1] / resolution[1] )

uvs = getAllFaces(bbProject, ratio)
print(f"Processing {len(uvs)} faces")

for uv in uvs:
    subMap = heightMap[uv[1][0]:uv[1][1],uv[0][0]:uv[0][1]]
    normalMap[uv[1][0]:uv[1][1],uv[0][0]:uv[0][1]] = computeNormalMap(subMap, scale)

print(f"Conversion complete")

normalTexture = {}
selectedTexture = 0

if not normalMapPath:
    if not auto:
        print(f"Select normalmap:")
        for tx in allTextures:
            print(f"\t{selectedTexture}: {tx['name']} [{tx['width']}x{tx['height']}]")
            selectedTexture += 1
        print(f"\t{selectedTexture}: Create new entry")
        selectedTexture = int(input("normalmap> "))
    else:
        for tx in allTextures:
            if tx['name'].endswith("_n.png") or tx['name'].endswith("_n"):
                break
            selectedTexture += 1
            
    if selectedTexture >= len(allTextures):
        normalTexture = copy.deepcopy(heightMapTexture)
        normalTexture['id'] = str(len(allTextures) + 1)
        normalTexture['uuid'] = str(uuid.uuid4())
        normalTexture['name'] = prepNameForNormal(heightMapTexture['name'])
        if 'path' in heightMapTexture:
            normalTexture['path'] = prepNameForNormal(heightMapTexture['path'])
        if 'relative_path' in heightMapTexture:
            normalTexture['relative_path'] = prepNameForNormal(heightMapTexture['relative_path'])
        print(f"No existing normal texture in model, creating new entry {normalTexture['name']}")
        allTextures.append(normalTexture)
    else:
        normalTexture = allTextures[selectedTexture]
        print(f"Overwriting existing texture {normalTexture['name']}")

    normalTexture['width'] = heightMapTexture['width']
    normalTexture['height'] = heightMapTexture['height']
    normalTexture['visible'] = True
    normalTexture['internal'] = True
    normalTexture['saved'] = False
    normalTexture['layers_enabled'] = False
    normalTexture['source'] = imageToBlob(normalMap)

    if 'layers' in normalTexture:
        normalTexture.pop('layers')

    if 'path' in normalTexture and os.path.isfile(normalTexture['path']):
        if auto or inputFor("Overwrite disk copy? [y/n]> ", ["y", "n"]) == "y":
            mpimg.imsave(normalTexture['path'], arr=normalMap, format="PNG")
            normalTexture['saved'] = True
            print(f"Overwrote texture on disk at {normalTexture['path']}")

    with open(bbProjectPath, 'w') as file:
        json.dump(bbProject, file)
        print(f"Saved changes to project file")
else:
    print(f"Saving as {normalMapPath}")
    mpimg.imsave(normalMapPath, arr=normalMap, format="PNG")

print(f"{Fore.CYAN}Conversion Complete{Style.RESET_ALL}")
