package com.petshop.repository;

import com.petshop.model.Cliente;
import com.petshop.model.Pets;
import com.petshop.model.Raca;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PetsRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PetsRepository petsRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RacaRepository racaRepository;

    private Cliente cliente1;
    private Cliente cliente2;
    private Raca raca1;
    private Raca raca2;
    private Pets pet1;
    private Pets pet2;
    private Pets pet3;

    @BeforeEach
    void setUp() {
        cliente1 = new Cliente();
        cliente1.setNome("João Silva");
        cliente1.setCpf("12345678901");
        cliente1 = clienteRepository.save(cliente1);

        cliente2 = new Cliente();
        cliente2.setNome("Maria Santos");
        cliente2.setCpf("98765432100");
        cliente2 = clienteRepository.save(cliente2);

        raca1 = new Raca();
        raca1.setDescricao("Golden Retriever");
        raca1 = racaRepository.save(raca1);

        raca2 = new Raca();
        raca2.setDescricao("Labrador");
        raca2 = racaRepository.save(raca2);

        pet1 = new Pets();
        pet1.setNome("Rex");
        pet1.setDataNascimento(LocalDate.of(2020, 5, 15));
        pet1.setCliente(cliente1);
        pet1.setRaca(raca1);

        pet2 = new Pets();
        pet2.setNome("Max");
        pet2.setDataNascimento(LocalDate.of(2021, 3, 10));
        pet2.setCliente(cliente1);
        pet2.setRaca(raca2);

        pet3 = new Pets();
        pet3.setNome("Luna");
        pet3.setDataNascimento(LocalDate.of(2019, 8, 20));
        pet3.setCliente(cliente2);
        pet3.setRaca(raca1);

        petsRepository.saveAll(List.of(pet1, pet2, pet3));
        entityManager.flush();
    }

    @Test
    void findByClienteId_ShouldReturnPetsForClient() {
        List<Pets> result = petsRepository.findByClienteId(cliente1.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Pets::getCliente).allMatch(c -> c.getId().equals(cliente1.getId()));
        assertThat(result).extracting(Pets::getNome).containsExactlyInAnyOrder("Rex", "Max");
    }

    @Test
    void findByClienteId_WithNonExistentClient_ShouldReturnEmptyList() {
        List<Pets> result = petsRepository.findByClienteId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByNomeContaining_ShouldReturnPetsWithMatchingName() {
        String nome = "ex";

        List<Pets> result = petsRepository.findByNomeContaining(nome);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).containsIgnoringCase("ex");
    }

    @Test
    void findByNomeContaining_WithCaseInsensitive_ShouldReturnMatchingPets() {
        String nome = "MAX";

        List<Pets> result = petsRepository.findByNomeContaining(nome);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).containsIgnoringCase("max");
    }

    @Test
    void findByNomeContaining_WithNonExistentName_ShouldReturnEmptyList() {
        String nome = "Buddy";

        List<Pets> result = petsRepository.findByNomeContaining(nome);

        assertThat(result).isEmpty();
    }

    @Test
    void findByRacaId_ShouldReturnPetsForRace() {
        List<Pets> result = petsRepository.findByRacaId(raca1.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Pets::getRaca).allMatch(r -> r.getId().equals(raca1.getId()));
        assertThat(result).extracting(Pets::getNome).containsExactlyInAnyOrder("Rex", "Luna");
    }

    @Test
    void findByRacaId_WithNonExistentRace_ShouldReturnEmptyList() {
        List<Pets> result = petsRepository.findByRacaId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByDataNascimento_ShouldReturnPetsWithExactBirthDate() {
        LocalDate dataNascimento = LocalDate.of(2020, 5, 15);

        List<Pets> result = petsRepository.findByDataNascimento(dataNascimento);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDataNascimento()).isEqualTo(dataNascimento);
        assertThat(result.get(0).getNome()).isEqualTo("Rex");
    }

    @Test
    void findByDataNascimento_WithNonExistentDate_ShouldReturnEmptyList() {
        LocalDate dataNascimento = LocalDate.of(2025, 1, 1);

        List<Pets> result = petsRepository.findByDataNascimento(dataNascimento);

        assertThat(result).isEmpty();
    }

    @Test
    void findByDataNascimentoBetween_ShouldReturnPetsInBirthDateRange() {
        LocalDate dataInicio = LocalDate.of(2020, 1, 1);
        LocalDate dataFim = LocalDate.of(2020, 12, 31);

        List<Pets> result = petsRepository.findByDataNascimentoBetween(dataInicio, dataFim);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("Rex");
        assertThat(result.get(0).getDataNascimento()).isEqualTo(LocalDate.of(2020, 5, 15));
    }

    @Test
    void findByDataNascimentoBetween_WithWideRange_ShouldReturnAllPets() {
        LocalDate dataInicio = LocalDate.of(2019, 1, 1);
        LocalDate dataFim = LocalDate.of(2021, 12, 31);

        List<Pets> result = petsRepository.findByDataNascimentoBetween(dataInicio, dataFim);

        assertThat(result).hasSize(3);
    }

    @Test
    void findByClienteIdAndNomeContaining_ShouldReturnPetsForClientWithMatchingName() {
        String nome = "ex";

        List<Pets> result = petsRepository.findByClienteIdAndNomeContaining(cliente1.getId(), nome);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCliente().getId()).isEqualTo(cliente1.getId());
        assertThat(result.get(0).getNome()).containsIgnoringCase("ex");
    }

    @Test
    void findByClienteIdAndNomeContaining_WithNonExistentClient_ShouldReturnEmptyList() {
        String nome = "Rex";

        List<Pets> result = petsRepository.findByClienteIdAndNomeContaining(999L, nome);

        assertThat(result).isEmpty();
    }

    @Test
    void findByClienteIdAndRacaId_ShouldReturnPetsForClientAndRace() {
        List<Pets> result = petsRepository.findByClienteIdAndRacaId(cliente1.getId(), raca1.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCliente().getId()).isEqualTo(cliente1.getId());
        assertThat(result.get(0).getRaca().getId()).isEqualTo(raca1.getId());
        assertThat(result.get(0).getNome()).isEqualTo("Rex");
    }

    @Test
    void findByClienteIdAndRacaId_WithNonExistentCombination_ShouldReturnEmptyList() {
        List<Pets> result = petsRepository.findByClienteIdAndRacaId(999L, 999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByNomeContainingAndRacaId_ShouldReturnPetsWithMatchingNameAndRace() {
        String nome = "ex";

        List<Pets> result = petsRepository.findByNomeContainingAndRacaId(nome, raca1.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).containsIgnoringCase("ex");
        assertThat(result.get(0).getRaca().getId()).isEqualTo(raca1.getId());
    }

    @Test
    void findByNomeContainingAndRacaId_WithNonExistentRace_ShouldReturnEmptyList() {
        String nome = "Rex";

        List<Pets> result = petsRepository.findByNomeContainingAndRacaId(nome, 999L);

        assertThat(result).isEmpty();
    }

    @Test
    void save_ShouldPersistPet() {
        Pets novoPet = new Pets();
        novoPet.setNome("Buddy");
        novoPet.setDataNascimento(LocalDate.of(2022, 1, 1));
        novoPet.setCliente(cliente1);
        novoPet.setRaca(raca2);

        Pets saved = petsRepository.save(novoPet);
        entityManager.flush();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNome()).isEqualTo("Buddy");
        assertThat(saved.getCliente().getId()).isEqualTo(cliente1.getId());
        assertThat(saved.getRaca().getId()).isEqualTo(raca2.getId());
    }

    @Test
    void deleteById_ShouldRemovePet() {
        Long petId = pet1.getId();

        petsRepository.deleteById(petId);
        entityManager.flush();

        assertThat(petsRepository.findById(petId)).isEmpty();
    }

    @Test
    void deleteByRacaId_ShouldRemoveAllPetsForRace() {
        Long racaId = raca1.getId();

        petsRepository.deleteByRacaId(racaId);
        entityManager.flush();

        assertThat(petsRepository.findByRacaId(racaId)).isEmpty();
        assertThat(petsRepository.findByRacaId(raca2.getId())).hasSize(1);
    }

    @Test
    void findAll_ShouldReturnAllPets() {
        List<Pets> result = petsRepository.findAll();

        assertThat(result).hasSize(3);
        assertThat(result).extracting(Pets::getNome).containsExactlyInAnyOrder("Rex", "Max", "Luna");
    }

    @Test
    void findById_ShouldReturnSpecificPet() {
        var result = petsRepository.findById(pet1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getNome()).isEqualTo("Rex");
        assertThat(result.get().getCliente().getId()).isEqualTo(cliente1.getId());
    }

    @Test
    void findById_WithNonExistentId_ShouldReturnEmpty() {
        var result = petsRepository.findById(999L);

        assertThat(result).isEmpty();
    }
}

