package com.petshop.repository;

import com.petshop.model.Atendimento;
import com.petshop.model.Cliente;
import com.petshop.model.Pets;
import com.petshop.model.Raca;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AtendimentoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PetsRepository petsRepository;

    @Autowired
    private RacaRepository racaRepository;

    private Cliente cliente;
    private Raca raca;
    private Pets pet;
    private Atendimento atendimento1;
    private Atendimento atendimento2;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");
        cliente = clienteRepository.save(cliente);

        raca = new Raca();
        raca.setDescricao("Golden Retriever");
        raca = racaRepository.save(raca);

        pet = new Pets();
        pet.setNome("Rex");
        pet.setDataNascimento(LocalDate.of(2020, 5, 15));
        pet.setCliente(cliente);
        pet.setRaca(raca);
        pet = petsRepository.save(pet);

        atendimento1 = new Atendimento();
        atendimento1.setPet(pet);
        atendimento1.setData(LocalDate.of(2024, 1, 15));
        atendimento1.setDescricao("Consulta veterinária");
        atendimento1.setValor(new BigDecimal("100.00"));

        atendimento2 = new Atendimento();
        atendimento2.setPet(pet);
        atendimento2.setData(LocalDate.of(2024, 2, 15));
        atendimento2.setDescricao("Vacinação");
        atendimento2.setValor(new BigDecimal("80.00"));

        atendimentoRepository.saveAll(List.of(atendimento1, atendimento2));
        entityManager.flush();
    }

    @Test
    void findByPetId_ShouldReturnAtendimentosForPet() {
        List<Atendimento> result = atendimentoRepository.findByPetId(pet.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Atendimento::getPet).allMatch(p -> p.getId().equals(pet.getId()));
    }

    @Test
    void findByPetId_WithNonExistentPet_ShouldReturnEmptyList() {
        List<Atendimento> result = atendimentoRepository.findByPetId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByData_ShouldReturnAtendimentosForDate() {
        LocalDate data = LocalDate.of(2024, 1, 15);

        List<Atendimento> result = atendimentoRepository.findByData(data);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getData()).isEqualTo(data);
        assertThat(result.get(0).getDescricao()).isEqualTo("Consulta veterinária");
    }

    @Test
    void findByData_WithNonExistentDate_ShouldReturnEmptyList() {
        LocalDate data = LocalDate.of(2025, 1, 1);

        List<Atendimento> result = atendimentoRepository.findByData(data);

        assertThat(result).isEmpty();
    }

    @Test
    void findByDataBetween_ShouldReturnAtendimentosInRange() {
        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
        LocalDate dataFim = LocalDate.of(2024, 1, 31);

        List<Atendimento> result = atendimentoRepository.findByDataBetween(dataInicio, dataFim);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getData()).isEqualTo(LocalDate.of(2024, 1, 15));
    }

    @Test
    void findByDataBetween_WithWideRange_ShouldReturnAllAtendimentos() {
        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
        LocalDate dataFim = LocalDate.of(2024, 12, 31);

        List<Atendimento> result = atendimentoRepository.findByDataBetween(dataInicio, dataFim);

        assertThat(result).hasSize(2);
    }

    @Test
    void findByValor_ShouldReturnAtendimentosWithExactValue() {
        BigDecimal valor = new BigDecimal("100.00");

        List<Atendimento> result = atendimentoRepository.findByValor(valor);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getValor()).isEqualTo(valor);
    }

    @Test
    void findByValorBetween_ShouldReturnAtendimentosInValueRange() {
        BigDecimal valorMin = new BigDecimal("50.00");
        BigDecimal valorMax = new BigDecimal("150.00");

        List<Atendimento> result = atendimentoRepository.findByValorBetween(valorMin, valorMax);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Atendimento::getValor)
                .allMatch(valor -> valor.compareTo(valorMin) >= 0 && valor.compareTo(valorMax) <= 0);
    }

    @Test
    void findByValorBetween_WithNarrowRange_ShouldReturnFilteredAtendimentos() {
        BigDecimal valorMin = new BigDecimal("90.00");
        BigDecimal valorMax = new BigDecimal("110.00");

        List<Atendimento> result = atendimentoRepository.findByValorBetween(valorMin, valorMax);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getValor()).isEqualTo(new BigDecimal("100.00"));
    }

    @Test
    void findByDescricaoContaining_ShouldReturnAtendimentosWithMatchingDescription() {
        String descricao = "consulta";

        List<Atendimento> result = atendimentoRepository.findByDescricaoContaining(descricao);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescricao()).containsIgnoringCase("consulta");
    }

    @Test
    void findByDescricaoContaining_WithCaseInsensitive_ShouldReturnMatchingAtendimentos() {
        String descricao = "VACINAÇÃO";

        List<Atendimento> result = atendimentoRepository.findByDescricaoContaining(descricao);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescricao()).containsIgnoringCase("vacinação");
    }

    @Test
    void findByDescricaoContaining_WithNonExistentDescription_ShouldReturnEmptyList() {
        String descricao = "cirurgia";

        List<Atendimento> result = atendimentoRepository.findByDescricaoContaining(descricao);

        assertThat(result).isEmpty();
    }

    @Test
    void findByPetIdAndData_ShouldReturnAtendimentoForPetAndDate() {
        LocalDate data = LocalDate.of(2024, 1, 15);

        List<Atendimento> result = atendimentoRepository.findByPetIdAndData(pet.getId(), data);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPet().getId()).isEqualTo(pet.getId());
        assertThat(result.get(0).getData()).isEqualTo(data);
    }

    @Test
    void findByPetIdAndData_WithNonExistentCombination_ShouldReturnEmptyList() {
        LocalDate data = LocalDate.of(2025, 1, 1);

        List<Atendimento> result = atendimentoRepository.findByPetIdAndData(pet.getId(), data);

        assertThat(result).isEmpty();
    }

    @Test
    void findByPetIdAndDataBetween_ShouldReturnAtendimentosForPetInDateRange() {
        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
        LocalDate dataFim = LocalDate.of(2024, 1, 31);

        List<Atendimento> result = atendimentoRepository.findByPetIdAndDataBetween(pet.getId(), dataInicio, dataFim);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPet().getId()).isEqualTo(pet.getId());
        assertThat(result.get(0).getData()).isEqualTo(LocalDate.of(2024, 1, 15));
    }

    @Test
    void findByPetIdAndDataBetween_WithWideRange_ShouldReturnAllAtendimentosForPet() {
        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
        LocalDate dataFim = LocalDate.of(2024, 12, 31);

        List<Atendimento> result = atendimentoRepository.findByPetIdAndDataBetween(pet.getId(), dataInicio, dataFim);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Atendimento::getPet).allMatch(p -> p.getId().equals(pet.getId()));
    }

    @Test
    void save_ShouldPersistAtendimento() {
        Atendimento novoAtendimento = new Atendimento();
        novoAtendimento.setPet(pet);
        novoAtendimento.setData(LocalDate.of(2024, 3, 15));
        novoAtendimento.setDescricao("Banho e tosa");
        novoAtendimento.setValor(new BigDecimal("60.00"));

        Atendimento saved = atendimentoRepository.save(novoAtendimento);
        entityManager.flush();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPet().getId()).isEqualTo(pet.getId());
        assertThat(saved.getData()).isEqualTo(LocalDate.of(2024, 3, 15));
        assertThat(saved.getDescricao()).isEqualTo("Banho e tosa");
        assertThat(saved.getValor()).isEqualTo(new BigDecimal("60.00"));
    }

    @Test
    void deleteById_ShouldRemoveAtendimento() {
        Long atendimentoId = atendimento1.getId();

        atendimentoRepository.deleteById(atendimentoId);
        entityManager.flush();

        assertThat(atendimentoRepository.findById(atendimentoId)).isEmpty();
    }
}

