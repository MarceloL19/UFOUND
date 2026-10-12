package pe.edu.ulima.ufound.analisis;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.ulima.ufound.repository.ObjetoPerdidoRepository;

import java.time.LocalDate;

@Service
public class AnalisisPerdidasService {

    private final ObjetoPerdidoRepository objetoPerdidoRepository;
    private final AnalisisPerdidasCalculadora calculadora;

    public AnalisisPerdidasService(ObjetoPerdidoRepository objetoPerdidoRepository, AnalisisPerdidasCalculadora calculadora) {
        this.objetoPerdidoRepository = objetoPerdidoRepository;
        this.calculadora = calculadora;
    }

    @Transactional(readOnly = true)
    public AnalisisPerdidasVista consultar(String periodo, LocalDate desde, LocalDate hasta) {
        return consultar(periodo, desde, hasta, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public AnalisisPerdidasVista consultar(String periodo, LocalDate desde, LocalDate hasta, LocalDate hoy) {
        return calculadora.analizar(objetoPerdidoRepository.findAll(), periodo, desde, hasta, hoy);
    }
}
