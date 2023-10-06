package com.zup.StudyGoals.application;

import com.zup.StudyGoals.application.mapper.RelatorioDTOMapper;
import com.zup.StudyGoals.data.MetaRepository;
import com.zup.StudyGoals.data.RelatorioRepository;
import com.zup.StudyGoals.domain.Meta;
import com.zup.StudyGoals.domain.Relatorio;
import com.zup.StudyGoals.dto.MaterialDeEstudoDTO;
import com.zup.StudyGoals.dto.MetaDTO;
import com.zup.StudyGoals.dto.RelatorioDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    @Autowired
    RelatorioRepository relatorioRepository;

    @Autowired
    MetaRepository metaRepository;

    public List<RelatorioDTO> listarRelatorios() {
        return relatorioRepository
                .findAll()
                .stream()
                .map(RelatorioDTOMapper.INSTANCE::relatorioParaDTO)
                .collect(Collectors.toList());
    }

    public Optional<RelatorioDTO> buscarRelatorioPorId(Long id) {
        return relatorioRepository.findById(id)
                .map(RelatorioDTOMapper.INSTANCE::relatorioParaDTO);
    }

    public RelatorioDTO cadastrarRelatorio(RelatorioDTO relatorioDTO) {
        Relatorio relatorio = new Relatorio();
        BeanUtils.copyProperties(relatorioDTO, relatorio);
        relatorioRepository.save(relatorio);
         return relatorioDTO;
    }

    public Optional<RelatorioDTO> alterarRelatorio(Long id, RelatorioDTO relatorioDTO) {
        Optional<Relatorio> relatorioOptional = relatorioRepository.findById(id);

        if (relatorioOptional.isPresent()) {
            Relatorio relatorio = relatorioOptional.get();

            BeanUtils.copyProperties(relatorioDTO, relatorio, "id");
            relatorioRepository.save(relatorio);

            return Optional.of(RelatorioDTOMapper.INSTANCE.relatorioParaDTO(relatorio));
        }
        else return Optional.empty();
    }

    public void deletarRelatorio(Long id) {
        relatorioRepository.deleteById(id);
    }

    public Boolean metaFoiConcluida(MetaDTO metaDTO) {
        if (metaDTO.getDataFinal().isBefore(LocalDateTime.now())) return Boolean.TRUE;
        else return Boolean.FALSE;
    }

    public Long calcularDiasParaMeta(MetaDTO metaDTO) {
        Duration duracao = Duration
                .between(LocalDateTime.now(), metaDTO.getDataFinal());
        return duracao.toDays();
    }

    //Calcular Categorias Mais Consumidas
    public String calcularCategoriasMaisConsumidas(MetaDTO metaDTO, MaterialDeEstudoDTO materialDeEstudoDTO) {
        int artigos = 0;
        int videos = 0;
        int audios = 0;
        int workshops = 0;
        int livros = 0;

        for (int i = 0; !metaDTO.getMateriaisDeEstudo().isEmpty(); i++) {
            if (metaDTO.getMateriaisDeEstudo().get(i).equals("ARTIGO")){
                artigos += 1;
            }
            if (metaDTO.getMateriaisDeEstudo().get(i).equals("VIDEO")){
                videos += 1;
            }
            if (metaDTO.getMateriaisDeEstudo().get(i).equals("AUDIO")){
                audios += 1;
            }
            if (metaDTO.getMateriaisDeEstudo().get(i).equals("WORKSHOP")){
                workshops += 1;
            }
            if (metaDTO.getMateriaisDeEstudo().get(i).equals("LIVRO")){
                livros += 1;
            }
        }

        Map<String, Integer> categoriasValores = new HashMap<>();
        categoriasValores.put("ARTIGO", artigos);
        categoriasValores.put("VIDEO", videos);
        categoriasValores.put("AUDIO", audios);
        categoriasValores.put("WORKSHOP", workshops);
        categoriasValores.put("LIVRO", livros);

        List<Map.Entry<String, Integer>> categoriasOrdem = new ArrayList<>(categoriasValores.entrySet());

        categoriasOrdem.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        Map.Entry<String, Integer> primeiroPar = categoriasOrdem.get(0);

        String maisConsumida = primeiroPar.getKey() + " (" + primeiroPar.getValue() + ")";

        return maisConsumida;
    }

    //Calcular Tempo Total Dedicado
    public long calcularTempoTotalDedicado() {

        MetaDTO metaDTO = new MetaDTO();
        MaterialDeEstudoDTO materialDeEstudoDTO = new MaterialDeEstudoDTO();

        long duracaoMinutos = 0L;

        while (!metaDTO.getMateriaisDeEstudo().isEmpty()) {
            Duration duration = Duration.between(materialDeEstudoDTO.getDataInicio(), materialDeEstudoDTO.getDataConclusao());
            duracaoMinutos += (duration.getSeconds() * 60);
        }

        return duracaoMinutos;
    }

    //Calcular Média de Tempo Diária
    public long calcularMediaTempoDiaria() {
        long tempoTotal = calcularTempoTotalDedicado();
        if (tempoTotal <= 1440) {
            return tempoTotal;
        }

        int dias = 0;

        while (tempoTotal > 1440) {
            dias += 1;
            tempoTotal -= 1440;
        }

        return tempoTotal / dias;

    }

    //Calcular Resumos Feitos
    public int calcularResumosFeitos(MetaDTO metaDTO) {
        int resumosFeitos = 0;
        while (!metaDTO.getMateriaisDeEstudo().isEmpty()) {
            resumosFeitos += 1;
        }
        return resumosFeitos;
    }
}
