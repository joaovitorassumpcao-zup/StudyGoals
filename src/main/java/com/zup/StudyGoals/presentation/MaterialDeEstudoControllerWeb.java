package com.zup.StudyGoals.presentation;

import com.zup.StudyGoals.application.MaterialDeEstudoService;
import com.zup.StudyGoals.application.mapper.MaterialDeEstudoDTOMapper;
import com.zup.StudyGoals.domain.MaterialDeEstudo;
import com.zup.StudyGoals.dto.MaterialDeEstudoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping (path = "/api/materiais")
public class MaterialDeEstudoControllerWeb {

    @Autowired
    MaterialDeEstudoService materialDeEstudoService;

    //ENDPOINTS GET
    //get all
    @GetMapping
    public List<MaterialDeEstudoDTO> listarTodosOsMateriais(){
        return materialDeEstudoService.listarMateriais();
    }

    // get by id
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarMaterialPorId(@PathVariable Long id){

        Optional<MaterialDeEstudoDTO> material = materialDeEstudoService.buscarMaterialPorId(id);
        if(material.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("O material não foi encontrado. ");

        return ResponseEntity.ok().body(material);
    }

    //ENDPOINT POST
    @PostMapping("/{id}")
    public ResponseEntity<?> criarMaterial (@RequestParam Long metaId,@RequestBody MaterialDeEstudoDTO materialDeEstudoDTO){

        MaterialDeEstudo novoMaterial = new MaterialDeEstudo();
        novoMaterial.setTitulo(materialDeEstudoDTO.getTitulo());
        novoMaterial.setCategoria(materialDeEstudoDTO.getCategoria());
        novoMaterial.setUrl(materialDeEstudoDTO.getUrl());
        novoMaterial.setResumo(materialDeEstudoDTO.getResumo());
        novoMaterial.setDataInicio(materialDeEstudoDTO.getDataInicio());
        novoMaterial.setDataConclusao(materialDeEstudoDTO.getDataConclusao());

        //Long metaId = materialDeEstudoDTO.getMetas().getId();

        materialDeEstudoService.cadastrarMaterial(novoMaterial, metaId);

        return ResponseEntity.ok().body("Novo material de estudo cadastrado com sucesso! ");

    }

    //ENDPOINT PUT
    @PutMapping("/{id}")
    public ResponseEntity<?> alterarMaterial(@PathVariable Long id, @RequestBody MaterialDeEstudo materialDeEstudo){

        MaterialDeEstudo materialAlterado = materialDeEstudoService.alterarMaterial(id,materialDeEstudo);

        if(materialDeEstudo != null) return ResponseEntity.ok().body("Material de estudo editado com sucesso! ");

        return ResponseEntity.notFound().build();
    }

    //ENDPOINT DELETE
    @DeleteMapping("/{id}")
    public void deletarMaterial (@PathVariable Long id) {
        materialDeEstudoService.deletarMaterial(id);
    }
}
