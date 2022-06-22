package froogleApp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;

import classes.Termo;
import classes.Documentos;
import classes.HashTableStopWords;
import classes.ListaDoc;
import classes.Ordenacao;
import classes.StopWord;

public class FroogleApp {
	// DECLARANDO VARIAVEIS GLOBAIS->>
	
	//vetor de pontuações
	static final String[] pontuacao = {",", ".", "!", "-", "_", ";"};

	//arquivo de StopWords
	static final String arqStopWords = "stopWords.txt";

	//tabela Hash para StopWords
	static HashTableStopWords table = new HashTableStopWords(263);

	// Nome do arquivo que tem os nomes dos arquivos usados para execução
	static final String nomesDeArquivos = "nomesArquivos.txt";

	// Nome do arquivo que contem todos os termos catalogados
	static final String arquivoTermos = "termos.txt";

	// variavel para controlar a posição do vetor de termos
	static int posicao = 0;

	// varaivel para atribuição de id(identificador) aos Termos
	static int idTermo = 0;

	// vetor de termos (ideal trocar por arrayList posteriormente)
	static Termo[] aTermos = new Termo[1000];

	static Ordenacao ordenar = new Ordenacao();

	// ------------------------------------------------------

	// Métodos auxiliares do programa ->>>

	// #region Termos&Relacionados
	/**
	 * Método que utiliza uma classe externa chamada Termo,
	 * lê um arquivo de texto, cria um termo novo para cada palavra
	 * nova lida e atualiza a quantidade em palavras que ele ocorre.
	 * @param leitor -> variável scanner para leitura do arquivo
	 * @return vetor de termos com todos os termos criados
	 * @throws InterruptedException 
	 */
	public static Termo[] criarTermos(Documentos nomesArquivos) throws FileNotFoundException {

		Scanner leitor = new Scanner(new File(nomesArquivos.Titulo));

		/* variavel para receber as posições das palavras que se repetirem, quando as
		 * mesmas forem encontradas
		 */
		int iPosicaoPalavraRepete = 0;

		while (leitor.hasNext()) {// enquanto existir linhas para ler...

			/* vetor para receber as palavras de cada linha do arquivo, separadas por um
			 * espaço em branco
			 */
			String[] sPalavras = leitor.nextLine().split(" ");
		
			// laço para percorrer todas as palavras da linha
			for (int x = 0; x < sPalavras.length; x++) {
				//percorrer pontuações para retirá-las das palavras 
				for(int i = 0; i < pontuacao.length; i++){
					if(sPalavras[x].contains(pontuacao[i])){
						sPalavras[x] = sPalavras[x].substring(0,sPalavras[x].length()-1);
					}
				}
				
				/* se o retorno for igual a null, significa que essa palavra
				 * não esta na tabela de StopWords
				 */
				if (table.buscar(sPalavras[x]) == null) {
				
					boolean bPalavraRepete = false;// variavel de controle

					// variavel para controlar a leitura do vetor de termos 
					int y = 0;

					// laço para percorrer o vetor de termos enquanto ainda existirem termos salvos
					while (aTermos[y] != null) {
						if (aTermos[y].Palavra.equals(sPalavras[x])) {

							bPalavraRepete = true;
							iPosicaoPalavraRepete = y;
							break;
							// para a verificação quando é encontrada uma palavra igual, depois passa pra próxima
						}
						y++;
					}

					if (bPalavraRepete) {
						aTermos[iPosicaoPalavraRepete].NumeroDeOcorrencias++;
						if (!aTermos[iPosicaoPalavraRepete].listaDoc.verificarSeExisteDoc(nomesArquivos.Titulo)) {
							aTermos[iPosicaoPalavraRepete].listaDoc.inserirDocNoFim(nomesArquivos);
						}
						else 
							aTermos[iPosicaoPalavraRepete].listaDoc.fim.dado.ocorrenciasNesteDocOuValorDoDoc++;
					}
					else {
						Termo novoTermo = new Termo(idTermo, sPalavras[x], 1);// criando objeto termo
						Documentos novoDocumento = new Documentos(nomesArquivos.IdDoc, nomesArquivos.Titulo, 1);
						novoTermo.listaDoc.inserirDocNoFim(novoDocumento);
						aTermos[posicao] = novoTermo;// vetor de Termos recebe termo criado
						idTermo += 1;// Acresenta o id do termo conforme é criado.

						/* a variável de controle de posição só é atualizada após ser criado um novo
						 * termo
						 */
						posicao++;
					}
				}
			}
		}
		leitor.close();// fechamento do scanner de arquivo
		return aTermos;// retorna array de objetos Termos criados / catalogados
	}

	/**
	 * Percorre o vetor de Termos criados e mostra na tela as seguinte propriedades
	 * da classe Termo:
	 * -> ID;
	 * -> palavra;
	 * -> quantidade de vezes que ele ocorre;
	 * -> lista de documetos que esse termo aparece.
	 * 
	 * @param aTermos -> vetor de termos criados
	 * @return void
	 */
	public static void mostrarTermos(Termo[] aTermos) {
		for (Termo termos : aTermos) {
			// verifica se existe termo na posição
			if (termos != null) {
				System.out.println("==================================");
				System.out.println(
						"\nID: " + termos.IdTermo + "\nTermo: " + termos.Palavra + "\nOcorrências: " + termos.NumeroDeOcorrencias + "\n"
								+ termos.listaDoc.imprimir());
				System.out.println("==================================");
			}
		}
	}

	/**
	 * Método que formata uma string com os dados do termo da posição passada como
	 * parâmetro
	 * no seguinte formato:
	 * -> (Id do termo);
	 * -> (Palavra (próprio termo));
	 * -> (Quantidade de vezes que esse termo ocorre);
	 * -> (lista de documetos que esse termo aparece).
	 * 
	 * @param pos (Posição do termo no vetor de termos (aTermos))
	 * @return resultBusca (String com os dados do termo naquela posição)
	 */
	public static String exibirTermo(int pos) {
		// formatando os dados do termo encontrado para poder retorna-lo para o usario.
		StringBuilder resultBusca = new StringBuilder("=============\n");
		resultBusca.append("ID: " + aTermos[pos].IdTermo + "\n");
		resultBusca.append("Termo: " + aTermos[pos].Palavra + "\n");
		resultBusca.append("Repete-se: " + aTermos[pos].NumeroDeOcorrencias + " vezes" + "\n");
		resultBusca.append(aTermos[pos].listaDoc.imprimir());

		return resultBusca.toString();
	}

	/**
	 * Método que formata uma string com a lista de documentos em que uma determinada
	 * palavra-chave aparece
	 * @param pos (Posição do termo no vetor de termos (aTermos))
	 * @return resultBusca (String com a lista de documentos do termo naquela posição)
	 */
	public static String exibirDocs(int pos) {
		// formatando os documentos do termo encontrado para poder retorna-lo para o usario.
		StringBuilder resultBusca = new StringBuilder("");
		resultBusca.append(aTermos[pos].listaDoc.imprimir());

		return resultBusca.toString();
	}

	/**
	 * Método que realiza uma busca no vetor de termos baseado na propriedade palavra
	 * @param termoDigitado -> string (Termo) que se deseja buscar
	 * @return int contendo a posição do termo procurado no vetor de termos ou -1, 
	 * caso esse termo não exista
	 */
	public static int buscarTermo(String termoDigitado) {
		int pos = 0;
		for (Termo objto : aTermos) {// para cada objeto no vetor de termos
			if (objto != null && objto.Palavra.equals(termoDigitado)) {
				// retorna posição do TERMO:
				return pos;
			}
			pos++;
		}

		return -1;
	}

	/**
	 * Cria um novo termo com a palavra digitada pelo usuário
	 * @param palavra -> string com a palavra que se deseja alocar na propriedade
	 * palavra da classe Termo
	 * @return void
	 */
	public static void inserirTermo(String palavra) {
		// verifica se o Termo cadastrado já não existe no vetor de Termos
		if (buscarTermo(palavra) == -1) {
			Termo novoTermo = new Termo(idTermo, palavra, 1);
			idTermo++;// atualiza o id do termo.

			aTermos[posicao] = novoTermo;
			System.out.print("Termo inserido com sucesso!");
			System.out.print("\nPalavra: " + aTermos[posicao].Palavra + "\nID:" + aTermos[posicao].IdTermo);
			posicao++;// atualiza a variável global de controle de posições do vetor de Termos.
		}

		else {
			System.out.print("Termo já existente. Por favor, insira um novo termo.");
		}
	}

	/**
	 * Método que limpa o vetor de termos (aTermos) percorrendo-o e atribuindo o
	 * valor null para cada posição onde antes havia um termo.
	 * No final, zeramos tanto a variável que controla a posição do vetor de termos
	 * quanto a que controla o id do termo (idTermo)
	 * @return void
	 */
	public static void limparVetor() {
		int i = 0;
		while (aTermos[i] != null) {
			aTermos[i] = null;
			i++;
		}
		
		posicao = 0;
		idTermo = 0;
	}
	// #endregion Termos&Relacionados.

	// #region Arquivos

	/**
	 * Método que percorre o arquivo nomesArquivos.txt que está formatado da
	 * seguinte forma:
	 * -> linha 1: número com a quantidade de nomes de aquivos
	 * -> a partir da linha 2: 1 nome de arquivo em cada linha
	 * @return nomeArquivos -> vetor da classe Documentos contendo os nomes de 
	 * arquivos a serem lidos pelo programa, na propriedade titulo
	 */
	public static Documentos[] carregarNomesDeArquivos() throws FileNotFoundException {

		Scanner lerNomes = new Scanner(new File(nomesDeArquivos));

		int qntArquivos = Integer.parseInt(lerNomes.nextLine());

		Documentos[] nomesArquivos = new Documentos[qntArquivos];

		int cont = 0;

		/* laço while para cada linha do arquivo depois da primeira linha, com a
		 * quantidade de arquivos
		 */
		while (lerNomes.hasNext()) {
			// antes de ler os termos dos documentos, eles receberão um número padrão de ocorrências: 1
			Documentos novoDoc = new Documentos(cont, lerNomes.nextLine(), 1);
			nomesArquivos[cont] = novoDoc;
			cont++;
		}

		lerNomes.close();

		return nomesArquivos;
	}

	/**
	 * Método que chama outro método para criar termos no vetor aTermos, os termos
	 * vêm das palavras dos arquivos cujo nomes estão no vetor recebido como parâmetro
	 * @param nomesArquivos -> vetor de objetos da classe Documentos que contem os 
	 * nomes referentes aos arquivos a serem carregados.
	 * @return void
	 */
	public static void carregarArquivos(Documentos[] nomesArquivos) throws FileNotFoundException {
		for (Documentos arq : nomesArquivos) {
			// catalogar e criar termos para cada arquivo passado como parâmetro
			criarTermos(arq);
		}
	}

	/**
	 * Método que formata as propriedade dos Termos e os salva em um arquivo.
	 * As propriedade são separadas por um ponto e vírgula (;) 
	 * São salvos no seguinte formato:
	 * IdTermo;Termo.palavra;Termo.NumeroDeOcorrencias;
	 * a partir daí, são gravados os documentos do termo, por isso,
	 * a quantidade de dados gravados na linha pode variar.
	 * São gravados da seguinte forma:
	 * IdDocumento;Documento.títilo;Ocorrências do termo naquele documento
	 * @return void
	 */
	public static void escreverTermosNoArquivo() throws IOException {
		// declarando arquivo termos.txt no java
		File arqTermos = new File(arquivoTermos);

		FileWriter sc = new FileWriter(arqTermos);

		// ordena de forma decrescente os termos antes de salvar
		ordenar.OrdenarTermos(aTermos, 0, posicao - 1);

		for (Termo objt : aTermos) {
			if (objt != null)
				sc.write(objt.IdTermo + ";" + objt.Palavra + ";" + objt.NumeroDeOcorrencias + objt.listaDoc.toString() + "\n");
		}

		sc.close();
	}

	/**
	 * Método que lê o arquivo de termos e cria um termo para cada linha lida
	 * @return void
	 */
	public static void carregarTermosDoArq() throws IOException {
		// declarando arquivo termos.txt no java
		File arqTermos = new File(arquivoTermos);
		Scanner lerTermos = new Scanner(arqTermos);

		String[] dataTermos;

		while (lerTermos.hasNext()) {
			dataTermos = lerTermos.nextLine().split(";");

			// das posições 0 a 2, os dados são fixos
			// IdTermo;Termo.palavra;Termo.NumeroDeOcorrencias;
			Termo termos = new Termo(Integer.parseInt(dataTermos[0]), dataTermos[1], Integer.parseInt(dataTermos[2]));

			/* após a posição 2, os dados gravados variam de acordo com a quantidade
			 * de documentos na lista, naquele determinado termo.
			 * IdDocumento;Documento.títilo;Ocorrências do termo naquele documento
			 * como temos 3 dados para cada documento, o laço abaixo excuta os
			 * comandos saltando de 3 em 3 posições
			 */
			for (int i = 3; i <= dataTermos.length - 3; i += 3) {
				termos.listaDoc.inserirDocNoFim(new Documentos(Integer.parseInt(dataTermos[i]), dataTermos[i + 1], Integer.parseInt(dataTermos[i + 2])));
			}

			aTermos[posicao] = termos;
			posicao++;
			idTermo++;
		}

		lerTermos.close();
	}
	// #endregion Arquivos

	// #region StopWords

	/**
	 * Método que lê o arquivo de stopword e os adciona à tabela hash de stopwords
	 * @param table -> tebela hash de strings
	 * @return void
	 */
	public static void carregarStopWords(HashTableStopWords table) throws IOException {
		Scanner readder = new Scanner(new File(arqStopWords));
		String stopWord = "";

		while (readder.hasNextLine()) {
			stopWord = readder.nextLine();
			StopWord novoTermoPalavra = new StopWord(stopWord);

			table.inserir(stopWord, novoTermoPalavra);
		}

		readder.close();
	}

	// #endregion StopWords

	// #region Menu
	/**
	 * Metodo para limpar tela 
	 * @return void
	 */
	public static void limparTela() {
		System.out.println("\n");
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}

	/**
	 * Método de menu do usuário que chama os métodos do programa de acordo
	 * com os opções escolhidas pelo usuário
	 * @return void
	 */
	public static void menu() throws IOException {
		Scanner entrada = new Scanner(System.in);

		// variavel de controle para o primeiro switch
		int opc1 = 0;

		// Opções do menu para o usuario.
		while (opc1 != 7) {
			System.out.println(
					"\n======================================\n		FROOGLE \n======================================\n");

			System.out.println("==> MENU PRINCIPAL <==");

			System.out.println("\n1. SALVAR TERMOS CATALOGADOS\n"
					+ "2. CONSULTAR TERMOS\n" + "3. CONSULTAR DOCUMENTOS\n" + "4. INSERIR NOVO TERMO\n"
					+ "5. CARREGAR NOVOS ARQUIVOS E CATALOGAR TERMOS\n" + "6. AJUDA\n" + "7. SAIR");

			System.out.print("Digite o numero da opção desejada:	");

			opc1 = entrada.nextInt();// opt recebe a opção escolida

			// receberá a posição dos termos buscados (opções 2 e 3)
			int termoPos = 0;

			switch (opc1) {// Switch na opção do usuario

				case 1:
					// escrever os termos no vetor aTermos no arquivo de termos formatado
					escreverTermosNoArquivo();
					System.out.print("Termos salvos com sucesso!");
					break;

				case 2:
					// CONSULTA DE TERMOS ->

					// Sub-Menu para consulta de Termos ->
					System.out.println("\n==> ESCOLHA UMA OPCAO <==");
					System.out.println("1. Consultar um termo especifico\n2. Ver todos os termos:	");
					
					// variavel para controle da opção do sub-menu do usuario
					int opc2 = 0;

					opc2 = entrada.nextInt();

					switch (opc2) {// Switch na opção do usuario

						case 1:
							// o método buscaTermo recebe o termo que o usuario deseja procurar
							String termoProcurado = null;

							System.out.print("\n=> Entre com o termo que deseja buscar:	");
							termoProcurado = entrada.next();

							// Procura e retorna a posição do termo ou -1 se o termo não existir
							termoPos = buscarTermo(termoProcurado);

							if (termoPos != -1) {
								System.out.println(exibirTermo(termoPos));
							}

							else {
								System.out.print("\nTermo não cadastrado.");
							}

							break;

						case 2:
							mostrarTermos(aTermos);// mostrar todos os termos
							break;

						default:
							System.out.println("Por favor, entre com uma opção valida.");
					}

					break;
				// FIM SUB-MENU

				case 3: 
					// BUSCA POR DOCUMENTOS ->

						// Sub-Menu para escolha de palavras-chave ->
						System.out.println("\n==> ESCOLHA UMA OPCAO <==");
						System.out.print("\n=> Você deseja buscar inserindo:\nUma palavra-chave (1)\nDuas palavras-chave (2)\n");
						
						int opc3 = 0;

						opc3 = entrada.nextInt();

						switch (opc3) {	
							case 1:
								// o método buscaTermo recebe o termo que o usuario deseja procurar 
								String palavraChave = null;

								System.out.print("\n=> Entre com a palavra-chave que deseja buscar nos documentos:	");
								palavraChave = entrada.next();

								// Procura e retorna a posição do termo ou -1 se o termo não existir
								termoPos = buscarTermo(palavraChave);

								if (termoPos != -1) {
									System.out.println(exibirDocs(termoPos));
								}

								else {
									System.out.print("\nNão aparece em nenhum documento.");
								}

								break;

							case 2:
								String[] palavrasChave = new String[2];
								System.out.print("\n=> Deseja utilizar pesos?\n (1) - sim\n (2) - não\n");

								int opc4 = entrada.nextInt();

								switch (opc4) {// sub-menu pesos
									case 1:
										ListaDoc listaDocsParaImprimir = new ListaDoc();
										int[] pesos = new int[2];
										System.out.print("\n=> Entre com a primeira palavra-chave que deseja buscar nos documentos: ");
										palavrasChave[0] = entrada.next();

										System.out.print("\n=> Entre com o peso da primeira palavra-chave: ");
										pesos[0] = entrada.nextInt();

										System.out.print("\n=> Entre com a segunda palavra-chave: ");
										palavrasChave[1] = entrada.next();

										System.out.print("\n=> Entre com o peso da segunda palavra-chave: ");
										pesos[1] = entrada.nextInt();

										termoPos = buscarTermo(palavrasChave[0]);
										if (termoPos != -1) {
											String[] documentos = aTermos[termoPos].listaDoc.toString().split(";");

											for(int i = 3; i < documentos.length; i+=3) {
												int ocorrenciasNesteDocumento = Integer.parseInt(documentos[i]); 
												
												Documentos novoDocParaImprimir = new Documentos(Integer.parseInt(documentos[i-2]), documentos[i-1], ocorrenciasNesteDocumento * pesos[0]);

												listaDocsParaImprimir.inserirDocNoFim(novoDocParaImprimir);
											}
										}
										else {
											System.out.print("\nPALAVRA-CHAVE 1: "+ palavrasChave[0] + "\nNão aparece em nenhum documento.\n");
										}

										termoPos = buscarTermo(palavrasChave[1]);
										if (termoPos != -1) {
											String[] documentos = aTermos[termoPos].listaDoc.toString().split(";");

											for(int i = 3; i < documentos.length; i+=3) {
												int ocorrenciasNesteDocumento = Integer.parseInt(documentos[i]); 
												
												Documentos novoDocParaImprimir = new Documentos(Integer.parseInt(documentos[i-2]), documentos[i-1], ocorrenciasNesteDocumento * pesos[1]);

												listaDocsParaImprimir.inserirDocNoFim(novoDocParaImprimir);
											}
										}
										else {
											System.out.print("\nPALAVRA-CHAVE 2: "+ palavrasChave[1] + "\nNão aparece em nenhum documento.\n");
										}

										Documentos[] arrayDocsParaImprimir = new Documentos[listaDocsParaImprimir.tamanho()];
										
										for (int i = 0; i < arrayDocsParaImprimir.length; i++) {
											arrayDocsParaImprimir[i] = listaDocsParaImprimir.retirarDocDoFim();
										}

										ordenar.OrdenarDocumentos(arrayDocsParaImprimir, 0, arrayDocsParaImprimir.length-1);

										for (int i = 0; i < arrayDocsParaImprimir.length; i++) {
											System.out.println(arrayDocsParaImprimir[i].imprimir());
										}

										break;

									case 2:
										System.out.print("\n=> Entre com a primeira palavra-chave que deseja buscar nos documentos: ");
										palavrasChave[0] = entrada.next();

										System.out.print("\n=> Entre com a segunda: ");
										palavrasChave[1] = entrada.next();

										termoPos = buscarTermo(palavrasChave[0]);
										if (termoPos != -1) {
											System.out.println("\nPALAVRA-CHAVE 1: " + palavrasChave[0] +"\n");
											System.out.println(exibirDocs(termoPos));
										}

										else {
											System.out.print("\nPALAVRA-CHAVE 1: "+ palavrasChave[0] + "\nNão aparece em nenhum documento.\n");
										}

										termoPos = buscarTermo(palavrasChave[1]);
										if (termoPos != -1) {
											System.out.println("\nPALAVRA-CHAVE 2: " + palavrasChave[1] +"\n");
											System.out.println(exibirDocs(termoPos));
										}

										else {
											System.out.print("\nPALAVRA-CHAVE 2: "+ palavrasChave[1] + "\nNão aparece em nenhum documento.\n");
										}
										break;
									
									default:
										System.out.println("Por favor, entre com uma opção valida.");
								}

								break;
							// fim do sub-menu

							default:
								System.out.println("Por favor, entre com uma opção valida.");
						}

					break;
				// FIM SUB-MENU

				case 4:
					/*-> O ARQUIVO CONTENDO OS TERMOS SÓ DEVE SER
					ATUALIZADO SE FOR ADD UM NOVO TERMO OU TODA VEZ QUE EXECUTAR O PROGRAMA?
					*/

					String novoTermo = null;
					System.out.print("Insira o termo que deseja adicionar:	");
					novoTermo = entrada.next();

					inserirTermo(novoTermo);
					break;

				case 5:
					// ler e carregar arquivos catalogando os termos:
					limparVetor();
					carregarArquivos(carregarNomesDeArquivos());
					System.out.print("Termos catalogados!");
					break;

				case 6:
					System.out.print("Na opção 1, você pode salvar os termos catalogados em um arquivo de texto.\n\n");
					System.out.print("Na opção 2, você pode pesquisar algum termo que desejar.\n\n");
					System.out.print("Na opção 3, você pode pesquisar algum documentos, inserindo uma ou duas palavras-chave.\n\n");
					System.out.print(
							"Na opçao 4, você pode inserir o novo termo. Lembre-se sempre de salvar, na opção 1, anstes de sair.\n\n");
					System.out.print(
							"Na opção 5, você pode catalogar termos de arquivos, você não precisa fazer isso após salvar os termos.\n"
									+ "Essa opção é necessária quando você adicionar novos arquivos de texto.");
					break;

				case 7:
					limparTela();
					System.out.println("\n\n==== OBRIGADO POR USAR O FROOGLE ==== > ==== VOLTE SEMPRE ====\n\n");
					break;

				default:
					System.out.println("Por favor, entre com uma opção valida.\n\n--\n\n");
					break;
			}

		} // FIM WHILE

		entrada.close();// fecha scanner do Menu
	}
	// #endregion Menu.

	// Método de execução do programa->>

	// #region Main
	public static void main(String[] args) throws IOException {
		//carregamos as StopWords para a Tabela Hash
		carregarStopWords(table);

		// carregar termos catalogados no arquivo texto(termos.txt) para o vetor de Termos
		carregarTermosDoArq();

		// chamar Menu do usuario
		menu();

	}
	// #endregion Main
}