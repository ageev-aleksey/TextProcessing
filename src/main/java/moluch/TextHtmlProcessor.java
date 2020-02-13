package moluch;

interface TextHtmlProcessor {
    enum Result {
        SUCCESSFUL, FAILURE
    }

    /**
     *
     * @param container Хештаблица, которая заполняется пользовательскими полями
     * @param txt_getter Объект у которого выполятеся получение очередной порции текста (TextNode) из DOM
     *                   дерева посредством вызова метода next()
     * @return Статус выполненого метода. SUCCESSFUL - удачно, текущее позиция в DOM дереве документа сохраняется.
     *            FAILURE - неудача, позиция в DOM дереве возвращается к положению до вызыва метода.
     *            После выполнения метода запускаетсы выполнение следующего метода, вне зависимости от того,
     *            выполнился ли данный метод удачно или нет.
     */
    Result execute( DomConsistentRunner.Container container, DomConsistentRunner.DataGetter txt_getter);
}