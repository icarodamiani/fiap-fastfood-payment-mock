# Parceiro de Pagamentos (Mock)

## Recursos e Bibliotecas
- [x] Java 17
- [x] Document DB
- [x] SQS
- [x] Spring Boot
- [x] MapStruct
- [x] Vavr
- [x] JsonPatch


## Dicionário de Linguagem Ubíqua

Termos utilizados na implementação (Presentes em Código)

- **Cliente/Customer**: O consumidor que realiza um pedido no restaurante.
- **Pedido/Order**: A lista de produtos (seja uma bebida, lanche, acompanhamento e/ou sobremesa) realizada pelo cliente no restaurante.
- **Produto/Product**: Item que será consumido pelo cliente, que se enquadra dentro de uma categoria, como por exemplo: bebida, lanche, acompanhamento e/ou sobremesa.
- **Categoria/Product Type**: Como os produtos são dispostos e gerenciados pelo estabelecimento: bebidas, lanches, acompanhamentos e/ou sobremesas.
- **Esteira de Pedidos/Order Tracking**: Responsável pelo andamento e monitoramento do estado do pedido.
- **Funcionário/Employee**: Funcionário do estabelecimento.

## [Pagamentos (Integração)]([payment-mock-api](payment-mock-api))
Ao receber pagamentos, esta api os encaminha a uma fila própria para consumo posterior. Desta forma emulando um processamento assíncrono dos pagamentos e "garantindo" maior resiliência ao processo.
Ao fim do processamento assíncro um Webhook é acionado para devolver a resposta do mesmo ao cliente que solicitou a operação inicial.

# Início rápido

```shell 
docker-compose up
```

A aplicação será disponibilizada em [localhost:8080](http://localhost:8080), tendo seu swagger em [localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).

# Deploy
O deploy das aplicações é feito e gerenciado através de Helm charts, estes localizados na pasta [charts](charts). Todos os charts apontam para imagens públicas e podem ser deployados em qualquer ordem. No entanto, em razão das dependências entre si, para que as aplicações estabilizem todos devem estar deployados.