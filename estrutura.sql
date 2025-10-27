--
-- PostgreSQL database dump
--

\restrict wzkkLC7FV7Tofreo8X3eFPcajNALsC7UMR55cle0zcF1YRqcfAQelbD5tj7lxJt

-- Dumped from database version 14.19 (Ubuntu 14.19-0ubuntu0.22.04.1)
-- Dumped by pg_dump version 14.19 (Ubuntu 14.19-0ubuntu0.22.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: atendimento; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.atendimento (
    id bigint NOT NULL,
    id_pet bigint NOT NULL,
    id_cliente bigint NOT NULL,
    descricao character varying(255) NOT NULL,
    valor numeric(10,2) NOT NULL,
    data date NOT NULL
);


ALTER TABLE public.atendimento OWNER TO postgres;

--
-- Name: atendimento_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.atendimento_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.atendimento_id_seq OWNER TO postgres;

--
-- Name: atendimento_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.atendimento_id_seq OWNED BY public.atendimento.id;


--
-- Name: cliente; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cliente (
    id bigint NOT NULL,
    nome character varying(100) NOT NULL,
    cpf character varying(14),
    data_cadastro date NOT NULL,
    foto character varying(255)
);


ALTER TABLE public.cliente OWNER TO postgres;

--
-- Name: cliente_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.cliente_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.cliente_id_seq OWNER TO postgres;

--
-- Name: cliente_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.cliente_id_seq OWNED BY public.cliente.id;


--
-- Name: contato; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contato (
    id bigint NOT NULL,
    id_cliente bigint NOT NULL,
    tag character varying(50),
    tipo character varying(20) NOT NULL,
    valor character varying(100) NOT NULL,
    CONSTRAINT contato_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['email'::character varying, 'telefone'::character varying])::text[])))
);


ALTER TABLE public.contato OWNER TO postgres;

--
-- Name: contato_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.contato_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.contato_id_seq OWNER TO postgres;

--
-- Name: contato_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.contato_id_seq OWNED BY public.contato.id;


--
-- Name: endereco; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.endereco (
    id bigint NOT NULL,
    id_cliente bigint NOT NULL,
    logradouro character varying(150) NOT NULL,
    cidade character varying(100) NOT NULL,
    bairro character varying(100),
    complemento character varying(100),
    tag character varying(50)
);


ALTER TABLE public.endereco OWNER TO postgres;

--
-- Name: endereco_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.endereco_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.endereco_id_seq OWNER TO postgres;

--
-- Name: endereco_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.endereco_id_seq OWNED BY public.endereco.id;


--
-- Name: pets; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pets (
    id bigint NOT NULL,
    id_cliente bigint NOT NULL,
    id_raca bigint,
    data_nascimento date,
    nome character varying(100) NOT NULL,
    foto character varying(255)
);


ALTER TABLE public.pets OWNER TO postgres;

--
-- Name: pets_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pets_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.pets_id_seq OWNER TO postgres;

--
-- Name: pets_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.pets_id_seq OWNED BY public.pets.id;


--
-- Name: raca; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.raca (
    id bigint NOT NULL,
    descricao character varying(100) NOT NULL
);


ALTER TABLE public.raca OWNER TO postgres;

--
-- Name: raca_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.raca_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.raca_id_seq OWNER TO postgres;

--
-- Name: raca_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.raca_id_seq OWNED BY public.raca.id;


--
-- Name: usuario; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuario (
    cpf character varying(14) NOT NULL,
    nome character varying(100) NOT NULL,
    perfil character varying(20) NOT NULL,
    senha character varying(255) NOT NULL,
    CONSTRAINT usuario_perfil_check CHECK (((perfil)::text = ANY ((ARRAY['CLIENTE'::character varying, 'ADMIN'::character varying])::text[])))
);


ALTER TABLE public.usuario OWNER TO postgres;

--
-- Name: atendimento id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.atendimento ALTER COLUMN id SET DEFAULT nextval('public.atendimento_id_seq'::regclass);


--
-- Name: cliente id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cliente ALTER COLUMN id SET DEFAULT nextval('public.cliente_id_seq'::regclass);


--
-- Name: contato id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contato ALTER COLUMN id SET DEFAULT nextval('public.contato_id_seq'::regclass);


--
-- Name: endereco id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.endereco ALTER COLUMN id SET DEFAULT nextval('public.endereco_id_seq'::regclass);


--
-- Name: pets id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pets ALTER COLUMN id SET DEFAULT nextval('public.pets_id_seq'::regclass);


--
-- Name: raca id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.raca ALTER COLUMN id SET DEFAULT nextval('public.raca_id_seq'::regclass);


--
-- Name: atendimento atendimento_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.atendimento
    ADD CONSTRAINT atendimento_pkey PRIMARY KEY (id);


--
-- Name: cliente cliente_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cliente
    ADD CONSTRAINT cliente_pkey PRIMARY KEY (id);


--
-- Name: contato contato_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contato
    ADD CONSTRAINT contato_pkey PRIMARY KEY (id);


--
-- Name: endereco endereco_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.endereco
    ADD CONSTRAINT endereco_pkey PRIMARY KEY (id);


--
-- Name: pets pets_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pets
    ADD CONSTRAINT pets_pkey PRIMARY KEY (id);


--
-- Name: raca raca_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.raca
    ADD CONSTRAINT raca_pkey PRIMARY KEY (id);


--
-- Name: usuario usuario_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_pkey PRIMARY KEY (cpf);


--
-- Name: atendimento fk62jakbs7n4b62qbkobfbruytq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.atendimento
    ADD CONSTRAINT fk62jakbs7n4b62qbkobfbruytq FOREIGN KEY (id_pet) REFERENCES public.pets(id);


--
-- Name: contato fk9g5kqiu1sb4yxs81ifvx4bgwp; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contato
    ADD CONSTRAINT fk9g5kqiu1sb4yxs81ifvx4bgwp FOREIGN KEY (id_cliente) REFERENCES public.cliente(id);


--
-- Name: atendimento fk_cliente; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.atendimento
    ADD CONSTRAINT fk_cliente FOREIGN KEY (id_cliente) REFERENCES public.cliente(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: pets fkdjbyrp6ck5iopndcmvmglr2ul; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pets
    ADD CONSTRAINT fkdjbyrp6ck5iopndcmvmglr2ul FOREIGN KEY (id_cliente) REFERENCES public.cliente(id);


--
-- Name: endereco fke4y3uftwnlua4wuwisldhnjl4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.endereco
    ADD CONSTRAINT fke4y3uftwnlua4wuwisldhnjl4 FOREIGN KEY (id_cliente) REFERENCES public.cliente(id);


--
-- Name: cliente fkqi38g2f9b2vsu5325necixv54; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cliente
    ADD CONSTRAINT fkqi38g2f9b2vsu5325necixv54 FOREIGN KEY (cpf) REFERENCES public.usuario(cpf);


--
-- Name: pets pets_id_raca_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pets
    ADD CONSTRAINT pets_id_raca_fkey FOREIGN KEY (id_raca) REFERENCES public.raca(id) ON DELETE SET NULL;


--
-- PostgreSQL database dump complete
--

\unrestrict wzkkLC7FV7Tofreo8X3eFPcajNALsC7UMR55cle0zcF1YRqcfAQelbD5tj7lxJt

