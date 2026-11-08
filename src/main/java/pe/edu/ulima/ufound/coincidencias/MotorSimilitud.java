package pe.edu.ulima.ufound.coincidencias;

import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.ObjetoPerdido;

public interface MotorSimilitud {

    int calcular(ObjetoPerdido perdido, ObjetoEncontrado encontrado);
}