## ✅ Implementado

### Acesso e Permissões
- [X] Apenas administradores e membros de RH podem entrar na aplicação.

### Integração com MQTT
- [X] Receber e decifrar dados do MQTT.

### Dashboard
- [X] Mostrar informações do sistema nos cartões do dashboard.
- [X] Atualizar os dados do dashboard a cada 20 segundos.

### Interface de Motoristas
- [X] Mostrar motoristas em cartões.

---

## 🛠️ Em Desenvolvimento

### Funcionalidades de Motoristas
- [X] Adicionar novo motorista.
- [X] Adicionar BPMs dos motoristas à base de dados recebidos via MQTT.
- [ ] Mostrar alertas passados dos motoristas.
- [X] Editar motorista.
- [X] Eliminar motorista.

### Melhorias Visuais e de UI
- [ ] Adicionar ícones aos botões de editar e eliminar.
- [ ] Alterar a cor de fundo do botão de eliminar.
- [ ] Mostrar uma borda vermelha e um badge no cartão do motorista quando os BPMs forem elevados.
- [X] Alterar a cor do pequeno ponto no cartão do motorista quando estiver offline.

### Dashboard e Estado do Sistema
- [ ] Mostrar o número real de alertas ativos no dashboard.
- [X] Adicionar estado da conexão MQTT.
- [X] Mostrar tempo restante para o refresh do dashboard.

---

## 🧠 Planeado / A Considerar

### Funcionalidades Futuras
- [X] Adicionar funcionalidade de logout.
- [X] Pensar num sistema de emparelhamento (pairing).
- [ ] Verificar tratamento de erros.
- [ ] Rever refresh dashboard quando da logout.

---

## 📝 Manutenção
- [ ] Tornar este README.md mais bonito.

## 📺 Outros
- [X] Fazer a apresentação.
- [X] Rever o uso de `ON DELETE CASCADE`, pois em alguns casos não faz sentido.
- [X] Traduzir as mensagens do código do ESP.