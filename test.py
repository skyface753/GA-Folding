import numpy as np

# Beispiel-Peptidsequenz
peptide_sequence = "HHPHHHPH"

# Erzeugen der Faltungsmatrix
folding_matrix = np.zeros((len(peptide_sequence), len(peptide_sequence)), dtype=int)
print(folding_matrix)
# Festlegen der Kontakte zwischen den Aminosäure-Resten
for i in range(len(peptide_sequence)):
    for j in range(i+1, len(peptide_sequence)):
        if peptide_sequence[i] == "H" and peptide_sequence[j] == "H":
            folding_matrix[i,j] = 1
            folding_matrix[j,i] = 1

# Ausgabe der Faltungsmatrix
print(folding_matrix)
