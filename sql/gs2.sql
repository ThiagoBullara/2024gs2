CREATE TABLE Gestor (
  id_gestor NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nome VARCHAR2(100) NOT NULL,
  email VARCHAR2(100) UNIQUE NOT NULL,
  telefone VARCHAR2(20) NOT NULL,
  descricao VARCHAR2(300) NOT NULL
);

CREATE TABLE Equipe (
  id_equipe NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nome VARCHAR2(100) UNIQUE NOT NULL,
  especialidade VARCHAR2(100) NOT NULL,
  email VARCHAR2(20) NOT NULL,
  descricao VARCHAR2(300) NOT NULL,
  qtd_funcionarios NUMBER NOT NULL
);

CREATE TABLE Recurso (
  id_recurso NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  tipo VARCHAR2(100) NOT NULL,
  quantidade NUMBER NOT NULL,
  custo_unitario NUMBER(10,2) NOT NULL,
  fornecedor VARCHAR2(100) NOT NULL,
  FK_id_projeto NUMBER NOT NULL,
  CONSTRAINT FK_id_projeto FOREIGN KEY (FK_id_projeto) REFERENCES Projeto (id_Projeto)
);

CREATE TABLE Projeto (
  id_projeto NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nome VARCHAR2(100) UNIQUE NOT NULL,
  tipo VARCHAR2(100) NOT NULL,
  descricao VARCHAR2(300) NOT NULL,
  status VARCHAR2(100) NOT NULL,
  localizacao VARCHAR2(300) NOT NULL,
  orcamento NUMBER(10,2) NOT NULL,
  duracao NUMBER NOT NULL,
  dataInicio DATE NOT NULL,
  dataTermino DATE NOT NULL,
  FK_id_gestor NUMBER NOT NULL,
  CONSTRAINT FK_id_gestor FOREIGN KEY (FK_id_gestor) REFERENCES Gestor (id_gestor),
  FK_id_equipe NUMBER NOT NULL,
  CONSTRAINT FK_id_equipe FOREIGN KEY (FK_id_equipe) REFERENCES Equipe (id_equipe)
);

CREATE TABLE Relatorio (
  id_relatorio NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  descricao VARCHAR2(300) NOT NULL,
  autor VARCHAR2(100) NOT NULL,
  dataEmissao DATE NOT NULL,
  qtd_energia_gerada NUMBER(10,2),
  eficiencia NUMBER(10,2),
  validade DATE,
  emissao_evitada NUMBER(10,2),
  recursos_economizados NUMBER(10,2),
  orcamento_geral NUMBER(10,2),
  despesas NUMBER(10,2),
  valorGerado NUMBER(10,2),
  FK_id_projeto NUMBER NOT NULL,
  CONSTRAINT FK_id_projeto FOREIGN KEY (FK_id_projeto) REFERENCES Projeto (id_Projeto)
);
