package br.com.blackseed.bimob.utils;

import android.graphics.pdf.PdfDocument;

        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.pdf.PdfDocument;
        import android.os.Environment;
        import android.support.annotation.Nullable;
        import android.text.Layout;
        import android.text.StaticLayout;
        import android.text.TextPaint;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
import java.util.Date;


public class LocacaoPdf {

    //A4 page size (N = 1/72 * inch)
    public static final int PAGE_WIDTH = 595;
    public static final int PAGE_HEIGHT = 842;
    public static final int MARGIN = 80;
    public static final int LEFT_MARGIN = 80;
    public static final int RIGHT_MARGIN = 80;
    public static final int TOP_MARGIN = 80;
    public static final int BOTTOM_MARGIN = 80;


    PdfDocument document = new PdfDocument();
    private String fileName = "/contrato";

    String mLocadorNome = "";
    String mLocadorCpf = "";
    String mLocadorEndereco = "";
    String mLocatarioNome = "";
    String mLocatarioCpf = "";
    String mLocatarioEndereco = "";
    String mFiadorNome = "";
    String mFiadorCpf = "";
    String mFiadorEndereco = "";
    String mImovelEndereco = "";
    String mImovelArea = "";
    String mImovelTipo = "";
    String mDataInicio = "";
    String mDataFim = "";
    String mPrazo = "";
    String mValor = "";
    String mValorExtenso = "";

    public LocacaoPdf (){

    }

    public void generatePDF(){

        PdfDocument.PageInfo pageinfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH,PAGE_HEIGHT,1).create();

        PdfDocument.Page pagina1 = document.startPage(pageinfo);
        drawPage1(pagina1);
        document.finishPage(pagina1);

        PdfDocument.Page pagina2 = document.startPage(pageinfo);
        drawPage2(pagina2);
        document.finishPage(pagina2);

        PdfDocument.Page pagina3 = document.startPage(pageinfo);
        drawPage3(pagina3);
        document.finishPage(pagina3);

        File root = new File(Environment.getExternalStorageDirectory(), "Contratos");
        if (!root.exists())
            root.mkdirs();



//        String path = Environment.getExternalStorageDirectory() + fileName;
        String path = root + fileName + (new Date()).getTime() + ".pdf";

        try {
            document.writeTo(new FileOutputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


//    private void drawPage1 (PdfDocument.Page page){
//        //Size units is in 1/72 * inch
//
//        Canvas canvas = page.getCanvas();
//
//        Paint paint = new Paint();
//        paint.setColor(Color.BLACK);
//        paint.setTextSize(16);
//
//        canvas.drawText("CONTRATO DE LOCAÇÃO DE IMÓVEL COMERCIAL", 100, 100, paint);
//
//    }

    private void drawPage1 (PdfDocument.Page page){
        //Size units is in 1/72 * inch

        Canvas canvas = page.getCanvas();

        TextPaint paint = new TextPaint();
        paint.setColor(Color.BLACK);

        paint.setTextSize(16);
        StaticLayout tituloSL = new StaticLayout(titulo, paint, PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1, 1, true);

        paint.setTextSize(12);
        StaticLayout corpoSL = new StaticLayout(buildPage1(), paint, PAGE_WIDTH, Layout.Alignment.ALIGN_NORMAL, 1, 1, true);

        tituloSL.draw(canvas);
        corpoSL.draw(canvas);

    }

    private void drawPage2 (PdfDocument.Page page){
        //Size units is in 1/72 * inch

        Canvas canvas = page.getCanvas();

        TextPaint paint = new TextPaint();
        paint.setColor(Color.BLACK);

        paint.setTextSize(12);
        StaticLayout corpoSL = new StaticLayout(buildPage2(), paint, PAGE_WIDTH, Layout.Alignment.ALIGN_NORMAL, 1, 1, true);

        corpoSL.draw(canvas);

    }

    private void drawPage3 (PdfDocument.Page page){
        //Size units is in 1/72 * inch

        Canvas canvas = page.getCanvas();

        TextPaint paint = new TextPaint();
        paint.setColor(Color.BLACK);

        paint.setTextSize(12);
        StaticLayout corpoSL = new StaticLayout(buildPage3(), paint, PAGE_WIDTH, Layout.Alignment.ALIGN_NORMAL, 1, 1, true);

        corpoSL.draw(canvas);

    }

    private String buildPage1(){
        StringBuilder itemsText = new StringBuilder();

        String intro = preencheLocador(introTemplate,
                mLocadorNome,
                mLocadorCpf,
                mLocadorEndereco);

        intro = preencheLocatario(intro,mLocatarioNome,
                mLocatarioCpf,
                mLocatarioEndereco);

        String objeto = preencheObjeto(objetoTemplate,
                mImovelEndereco,
                mImovelArea);

        String prazo = preenchePrazo(prazoTemplate, mPrazo, mDataInicio, mDataFim);

        String finalidade = preencheFinalidade(finalidadeTemplate, mImovelTipo);

        String preco = preenchePreco(precoTemplate, mValor, mValorExtenso);

//        String pagamento = preenchePagamento(pagamentoTemplate,
//                "1603-9",
//                "1584-9",
//                "001",
//                "Nilo Serafim Neto",
//                "10");

        itemsText.append("\n\n\n\t");
        itemsText.append(intro);
        itemsText.append("\n\n\t");
        itemsText.append(objeto);
        itemsText.append("\n\n\t");
        itemsText.append(prazo);
        itemsText.append("\n\n\t");
        itemsText.append(paragrafoPrimeiro);
        itemsText.append("\n\n\t");
        itemsText.append(paragrafoSegundo);
        itemsText.append("\n\n\t");
        itemsText.append(paragrafoTerceiro);
        itemsText.append("\n\n\t");
        itemsText.append(finalidade);
        itemsText.append("\n\n\t");
        itemsText.append(preco);
//        itemsText.append("\n\n\t");
//        itemsText.append(pagamento);

        return itemsText.toString();
    }

    private String buildPage2(){
        StringBuilder itemsText = new StringBuilder();

        itemsText.append("\n\n\n\t");
        itemsText.append(atraso);
        itemsText.append("\n\n\n\t");
        itemsText.append(reajuste);
        itemsText.append("\n\n\n\t");
        itemsText.append(uso);
        itemsText.append("\n\n\n\t");
        itemsText.append(benfeitorias);
        itemsText.append("\n\n\n\t");
        itemsText.append(exigencias);
        itemsText.append("\n\n\n\t");
        itemsText.append(sublocacao);
        itemsText.append("\n\n\n\t");
        itemsText.append(despesas);
        itemsText.append("\n\n\n\t");
        itemsText.append(vistoria);


        return itemsText.toString();
    }

    private String buildPage3() {
        StringBuilder itemsText = new StringBuilder();

        String fiador = preencheFiador(fiadorTemplate,
                mFiadorNome,
                mFiadorCpf,
                mFiadorEndereco);

        itemsText.append("\n\n\n\t");
        itemsText.append(recisao);
        itemsText.append("\n\n\n\t");
        itemsText.append(alienacao);
        itemsText.append("\n\n\n\t");
        itemsText.append(fiador);
        itemsText.append("\n\n\n\t");
        itemsText.append(substituicao);
        itemsText.append("\n\n\n\t");
        itemsText.append(infracao);
        itemsText.append("\n\n\n\t");
        itemsText.append(foro);
        itemsText.append("\n\n\n\t");
        itemsText.append(conclusao);

        return itemsText.toString();
    }

    private String preencheLocador(String paragrafo, String nome, String cpf, String endereco){

        paragrafo = paragrafo.replace("$locador$", maskIfNull(nome));
        paragrafo = paragrafo.replace("$cpf$", maskIfNull(cpf));
//        paragrafo = paragrafo.replace("$rg$", maskIfNull(rg));
//        paragrafo = paragrafo.replace("$estadoCivil$", maskIfNull(estadoCivil));
        paragrafo = paragrafo.replace("$endereco$", maskIfNull(endereco));

        return paragrafo;
    }

    private String preencheLocatario(String paragrafo, String nome, String cpf, String endereco){

        paragrafo = paragrafo.replace("$locatario$", maskIfNull(nome));
        paragrafo = paragrafo.replace("$cpf2$", maskIfNull(cpf));
//        paragrafo = paragrafo.replace("$rg2$", maskIfNull(rg));
//        paragrafo = paragrafo.replace("$estadoCivil2$", maskIfNull(estadoCivil));
        paragrafo = paragrafo.replace("$endereco2$", maskIfNull(endereco));

        return paragrafo;
    }

    private String preencheObjeto(String paragrafo, String endereco, String area){

        paragrafo = paragrafo.replace("$enderecoImovel$", maskIfNull(endereco));
        paragrafo = paragrafo.replace("$areaImovel$", maskIfNull(area));

        return paragrafo;
    }

    private String preenchePrazo (String paragrafo, String prazo, String inicio, String fim){

        paragrafo = paragrafo.replace("$prazo$", maskIfNull(prazo));
        paragrafo = paragrafo.replace("$dataInicio$", maskIfNull(inicio));
        paragrafo = paragrafo.replace("$dataFim$", maskIfNull(fim));

        return paragrafo;
    }

    private String preencheFinalidade (String paragrafo, String tipoImovel){

        paragrafo = paragrafo.replace("$tipoImovel$", maskIfNull(tipoImovel));

        return paragrafo;
    }

    private String preenchePreco (String paragrafo, String preco, @Nullable String precoExtenso){

        paragrafo = paragrafo.replace("$preco$", maskIfNull(preco));
        paragrafo = paragrafo.replace("$precoExtenso$", maskIfNull(precoExtenso));

        return paragrafo;
    }

    private String preenchePagamento (String paragrafo, String conta, String agencia, String banco, String titular, String vencimento){

        paragrafo = paragrafo.replace("$conta$", maskIfNull(conta));
        paragrafo = paragrafo.replace("$agencia$", maskIfNull(agencia));
        paragrafo = paragrafo.replace("$banco$", maskIfNull(banco));
        paragrafo = paragrafo.replace("$titular$", maskIfNull(titular));
        paragrafo = paragrafo.replace("$vencimento$", maskIfNull(vencimento));

        return paragrafo;

    }

    private String preencheFiador(String paragrafo, String nomeFiador, String cpfFiador, String enderecoFiador){

        paragrafo = paragrafo.replace("$nomeFiador$", maskIfNull(nomeFiador));
        paragrafo = paragrafo.replace("$cpfFiador$", maskIfNull(cpfFiador));
        paragrafo = paragrafo.replace("$enderecoFiador$", maskIfNull(enderecoFiador));

        return paragrafo;
    }

    private String maskIfNull(String string){
        if (string == null)
            string = " ";
        return string;
    }

    private String titulo = "CONTRATO DE LOCAÇÃO DE IMÓVEL COMERCIAL";

    private String introTemplate = "Pelo presente instrumento particular, de um lado, como " +
            "LOCADOR(A), $locador$, CPF $cpf$, domiciliado à $endereco$ " +
            "e, de outro lado, como LOCATÁRIO(A), $locatario$, CPF $cpf2$, " +
            "residente à $endereco2$, resolvem celebrar o " +
            "presente contrato de locação, o qual reger-se-á pelas seguintes cláusulas e condições:";

    private String objetoTemplate = "I. OBJETO: Constitui objeto do presente contrato a locação do " +
            "imóvel situado na $enderecoImovel$, com $areaImovel$m². Faz parte integrante deste " +
            "contrato, o laudo de vistoria prévia realizado e assinado pelas partes contratantes.";

    private String prazoTemplate = "II. PRAZO: O prazo de locação é de $prazo$, tendo início em " +
            "$dataInicio$. e término previsto para o dia $dataFim$.";

    private String paragrafoPrimeiro = "Parágrafo Primeiro: Se o (a) LOCATÁRIO (A), usando da " +
            "faculdade que lhe confere o artigo 4º. da lei n.º 8.245 de 18 do outubro de 1991, " +
            "devolver o imóvel locado antes do decorrido o prazo ajustado no caput desta cláusula, " +
            "pagará ao (a) LOCADOR(A) a multa compensatória correspondente a 03 (três) meses de " +
            "aluguel em vigor, reduzida proporcionalmente ao tempo do contrato já cumprido, na " +
            "forma do artigo 924 do código civil, na base de um doze 1/12 (um doze avos) para cada " +
            "mês já transcorrido.";

    private String paragrafoSegundo = "Parágrafo Segundo: Findo prazo acima ajustado, se o(a) " +
            "LOCATÁRIO(A) continuar no imóvel por mais de 30 (trinta) dias, sem oposição do(a) " +
            "LOCADOR(A), ficará a locação prorrogada automaticamente por prazo indeterminado, " +
            "nas mesmas bases contratuais; entretanto, o imóvel somente poderá ser retomado nos " +
            "casos previstos em lei, mas poderá ser devolvido pelo(a) LOCATÁRIO(A) a qualquer " +
            "tempo, sem a incidência de qualquer multa por este motivo, desde que mediante " +
            "comunicação prévia, por escrito, com antecedência mínima de 30 (trinta) dias, da " +
            "data da restituição do imóvel locado, sob pena de pagar a quantia correspondente a " +
            "um mês de aluguel e encargos vigentes.";

    private String paragrafoTerceiro = "Parágrafo Terceiro: Após o recebimento de pedido por " +
            "escrito do LOCATÁRIO, o LOCADOR terá o prazo de cinco dias para efetuar a vistoria " +
            "do imóvel, correndo por conta do LOCATÁRIO o aluguel até a efetiva devolução do " +
            "imóvel ao LOCADOR.";

    private String finalidadeTemplate = "III. FINALIDADE: O imóvel é locado para uso exclusivamente " +
            "$tipoImovel$.";

    private String precoTemplate = "IV. PREÇO E FORMA DE PAGAMENTO: O valor do aluguel mensal " +
            "é de $preco$ $precoExtenso$.";

    private String pagamentoTemplate = "Parágrafo Primeiro: Os alugueis estabelecidos no " +
            "\"caput\" desta cláusula deverão ser depositados na Conta/Corrente nº $conta$, " +
            "Agência nº $agencia$, Banco $banco$, em nome de $titular$, ou onde esta indicar, " +
            "por escrito, independentemente de aviso ou cobranças, todo dia $vencimento$ de cada mês.";

    private String atraso = "V. ATRASO NO PAGAMENTO: O não pagamento do aluguel no prazo ajustado " +
            "na cláusula 4ª implicará em multa de 2% (dois por cento) sobre o valor do débito, " +
            "juros de 1% (um por cento) ao mês e correção monetária calculada pelo IGPM da FGV.";

    private String reajuste = "VI. REAJUSTE DO ALUGUEL: O aluguel pactuado na cláusula anterior " +
            "sofrerá reajustes anuais com base na variação do Índice Geral de Preços divulgado " +
            "pela Fundação Getúlio Vargas (IGP-FGV) ou outro índice que porventura " +
            "venha a substituí-lo.";

    private String uso = "VII. USO DO IMÓVEL: A locatária obriga-se a manter o imóvel locado " +
            "em boas condições de higiene, limpeza e conservação, mantendo em perfeito estado " +
            "as suas instalações elétricas e hidráulicas, afim de restituí-lo no estado em que " +
            "recebeu, salvo as deteriorações decorrentes do uso normal.";

    private String benfeitorias = "VIII. BENFEITORIAS: Eventuais reformas ou adaptações que a " +
            "locatária pretender executar no imóvel, só poderão ser realizadas mediante " +
            "autorização prévia e expressa da locadora.";

    private String exigencias = "IX. EXIGÊNCIAS DOS PODERES PÚBLICOS: Obriga-se a locatária a " +
            "satisfazer a todas as exigências dos poderes públicos a que der causa.";

    private String sublocacao = "X. CESSÃO, SUBLOCAÇÃO E EMPRÉSTIMO: A locatária não poderá " +
            "transferir este contrato, ou sublocar o imóvel no todo ou em parte, sem prévia " +
            "autorização por escrito da locadora.";

    private String despesas = "XI. DESPESAS DE CONDOMÍNIO, CONSUMO E TAXAS: Todas as despesas " +
            "decorrentes da locação, quais sejam, consumo de água, luz, telefone e gás, prêmio " +
            "de seguro contra incêndio, IPTU, ficam a cargo da locatária, cabendo-lhe " +
            "efetuar diretamente esses pagamentos nas devidas épocas.";

    private String vistoria = "XII. VISTORIA: A locatária desde já faculta à locadora examinar " +
            "ou vistoriar o prédio, sempre que o segundo entender conveniente, desde que " +
            "previamente acordados dia e hora.";

    private String recisao = "XIII. RESCISÃO: O presente contrato ficará rescindido de pleno " +
            "direito, independentemente de qualquer notificação judicial ou extrajudicial e " +
            "sem que assista a nenhuma das partes o direito a qualquer indenização, ficando as " +
            "partes, daí por diante, desobrigadas por todas as cláusulas deste contrato, nos " +
            "seguintes casos:\n\n" +
            "a) Processo de desapropriação total ou parcial do imóvel locado;\n" +
            "b) Ocorrência de qualquer evento ou incêndio do imóvel locado que impeça a sua " +
            "ocupação, havendo ou não culpa do locatário e dos que estão sob sua responsabilidade;\n" +
            "c) Qualquer outro fato que obrigue o impedimento do imóvel locado, impossibilitando " +
            "a continuidade da locação.";

    private String alienacao = "XIV. ALIENAÇÃO DO IMÓVEL: Caso o imóvel objeto da locação for " +
            "alienado durante o prazo locatício, o adquirente fica obrigado a respeitar o " +
            "presente contrato.";

    private String fiadorTemplate = "XV. FIANÇA: Assina(m) também este contrato, solidariamente " +
            "com o locatária por todas obrigações firmadas, o(a) fiador(a) sr.(a) " +
            "$nomeFiador$, CPF $cpfFiador$, residente à $enderecoFiador$ cuja responsabilidade " +
            "subsistirá até a entrega efetiva das chaves do prédio locador.";

    private String substituicao = "XVI. SUBSTITUIÇÃO DA GARANTIA: No caso de morte, falência ou " +
            "insolvência do fiador, a locatária será obrigada, dentro de 30 (trinta) dias, " +
            "a substituir a garantia locatícia.";

    private String infracao = "XVII. INFRAÇÃO CONTRATUAL: A parte que infringir o presente " +
            "contrato pagará à parte inocente o valor correspondente a 3 (três) aluguéis vigentes " +
            "à época da infração, sem prejuízo de arcar com eventuais perdas e danos que " +
            "ocasionar e determinar a imediata rescisão do contrato.";

    private String foro = "XVIII. FORO: Para todas as questões decorrentes deste contrato, " +
            "será competente o foro da situação do imóvel, seja qual for o domicílio dos contratantes.";

    private String conclusao = "E, por estarem, assim ajustados, assinam o presente contrato " +
            "em 3 (três) vias, juntamente com duas testemunhas que a tudo assistiram, " +
            "para que possa surtir seus efeitos legais.";

    public String getLocadorNome() {
        return mLocadorNome;
    }

    public void setLocadorNome(String mLocadorNome) {
        this.mLocadorNome = mLocadorNome;
    }

    public String getLocadorCpf() {
        return mLocadorCpf;
    }

    public void setLocadorCpf(String mLocadorCpf) {
        this.mLocadorCpf = mLocadorCpf;
    }

    public String getLocadorEndereco() {
        return mLocadorEndereco;
    }

    public void setLocadorEndereco(String mLocadorEndereco) {
        this.mLocadorEndereco = mLocadorEndereco;
    }

    public String getLocatarioNome() {
        return mLocatarioNome;
    }

    public void setLocatarioNome(String mLocatarioNome) {
        this.mLocatarioNome = mLocatarioNome;
    }

    public String getLocatarioCpf() {
        return mLocatarioCpf;
    }

    public void setLocatarioCpf(String mLocatarioCpf) {
        this.mLocatarioCpf = mLocatarioCpf;
    }

    public String getLocatarioEndereco() {
        return mLocatarioEndereco;
    }

    public void setLocatarioEndereco(String mLocatarioEndereco) {
        this.mLocatarioEndereco = mLocatarioEndereco;
    }

    public String getFiadorNome() {
        return mFiadorNome;
    }

    public void setFiadorNome(String mFiadorNome) {
        this.mFiadorNome = mFiadorNome;
    }

    public String getFiadorCpf() {
        return mFiadorCpf;
    }

    public void setFiadorCpf(String mFiadorCpf) {
        this.mFiadorCpf = mFiadorCpf;
    }

    public String getFiadorEndereco() {
        return mFiadorEndereco;
    }

    public void setFiadorEndereco(String mFiadorEndereco) {
        this.mFiadorEndereco = mFiadorEndereco;
    }

    public String getImovelEndereco() {
        return mImovelEndereco;
    }

    public void setImovelEndereco(String mImovelEndereco) {
        this.mImovelEndereco = mImovelEndereco;
    }

    public String getImovelArea() {
        return mImovelArea;
    }

    public void setImovelArea(String mImovelArea) {
        this.mImovelArea = mImovelArea;
    }

    public String getImovelTipo() {
        return mImovelTipo;
    }

    public void setImovelTipo(String mImovelTipo) {
        this.mImovelTipo = mImovelTipo;
    }

    public String getmDataInicio() {
        return mDataInicio;
    }

    public void setDataInicio(String mDataInicio) {
        this.mDataInicio = mDataInicio;
    }

    public String getDataFim() {
        return mDataFim;
    }

    public void setDataFim(String mDataFim) {
        this.mDataFim = mDataFim;
    }

    public String getPrazo() {
        return mPrazo;
    }

    public void setPrazo(String mPrazo) {
        this.mPrazo = mPrazo;
    }

    public String getValor() {
        return mValor;
    }

    public void setValor(String mValor) {
        this.mValor = mValor;
    }

    public String getValorExtenso() {
        return mValorExtenso;
    }

    public void setValorExtenso(String mValorExtenso) {
        this.mValorExtenso = mValorExtenso;
    }
}