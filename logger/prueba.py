from estados import *

for estado in estados:
    if ((estado[20] + estado [21] == 1))\
    and ((estado[18] + estado[19]) == 1)\
    and ((estado[10] + estado[23] + estado[24] + estado [25] + estado[26] + estado [27]) == 1):
        print("estado valido")
    else:
        print("estado invalido")



