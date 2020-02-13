package moluch;

import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Класс оббертка для бибилотеки JSOUP, который предоставляет возможность вытаскивания текста, отделенного тегами.
 * Также предоставлает возможность обхода DOM дерева, с возвратом в исходную точку. Например, если метод выполяняте
 * движение по DOM дереву, и при этом метод завершился неудачей, возможно вернуться в ту же позицию DOM дерева, которое
 * было до выполнения метода. Или же остаться в той же позиции.
 * <p><b>Детали реализации:</b></p>
 * <p>Объект может, использовать <span color="red">много памяти</span> если обрабатываемый html документ имеет большой размер, так как выполняется
 * обход в глубину через стек (не рекурсия). Возврат к исходному состоянию реализуются путем сохранения стека ссылок
 * на непосещенных потомков.</p>
 */
class DomConsistentRunner {
    /**
     * Создание обработчкиа, который хранит в себе состояние (текущее не обработанный узел)
     * @param dom - DOM дерево html документа из которого начать вытаскивать текст.
     */
    DomConsistentRunner(Node dom) {
        methods = new ArrayList<Pair>();
        stack = new LinkedList<Node>();
        for (int i = dom.childNodes().size() - 1; i >= 0; i--) {
            stack.push(dom.childNode(i));
        }
    }
    /**
     *  Метод возращает следующий текст отделенный html тегом
     *  <pre>
     *{@code
     * Node dom = "<p>1<em>2<b>3</b></em>4</p>"
     * TextGetter tg = new TextGetter(dom);
     * //Напечатать весь текст, который содержит html страница
     * TextNode txt = new TextNode("");
     * while (txt != null) {
     *     System.out.println(txt);
     *     txt = tg.next();
     * }
     *  //OUTPUT:
     *  //1
     *  //2
     *  //3
     *  //4
     * }
     * </pre>
     */
    TextNode next() {
        while(!stack.isEmpty()) {
            String result = "";
            Node el = stack.pop();
            for (int i = el.childNodes().size() - 1; i >= 0; i--) {
                stack.push(el.childNode(i));
            }
            if (el instanceof TextNode) {
                return (TextNode)el;
            }
        }
        return null;
    }

    /**
     * регистрация объекта, который будет обрабатывать поток текста из Dom дерева html документа
     * @param method объект выполняющий обработку текста
     * @return иденификатор метода. Данный идентификатор будет добавляться к началу названия поля создаваемого объектом
     *          в хеш таблице при обработке текста. Это необходимо, поскольку разные методы могут создвать поля с
     *          одинаковым названием. Название будет формироваться по следующему шаблону:
     *          <br><b><идентификатор>_<название поля></b>
     */
    int add_method(TextHtmlProcessor method){
        int new_id = last_id++;
        methods.add(new Pair(method, new_id));
        return new_id;
    }

    /**
     * Метод запускает последовательное выполнение всех зарегистрированных обработчиков посредством метода add_method
     * @return Хештаблица создеращая поля, которые являются результатом работы запущенных методов.
     */
    HashMap<String, String[]> run() {
        HashMap<String, String[]> container = new HashMap<String, String[]>();
        DataGetter getter = new DataGetter(this);
        TextHtmlProcessor.Result res = TextHtmlProcessor.Result.SUCCESSFUL;
        for (Pair m : methods) {
            if (res == TextHtmlProcessor.Result.SUCCESSFUL) {
                stack_state = new LinkedList<Node>(stack);
            } else {
                stack = new LinkedList<Node>(stack_state);
            }
            res = m.method.execute(new Container(m.id, container), getter);
        }
        return container;
    }

    class DataGetter {
        DataGetter(DomConsistentRunner _main) {
            main = _main;
        }

        TextNode next() {
            return main.next();
        }

        private DomConsistentRunner main;
    }

    class Container {
        Container(int _id, HashMap<String, String[]> _map) {
            id = _id;
            map = _map;
        }
        public void put(String key, String[] value) {
            map.put(String.valueOf(id) + "_" + key, value);
        }
        private int id;
        private HashMap<String, String[]> map;
    }

    private class Pair {
        Pair(TextHtmlProcessor _method, int _id) {
            method = _method;
            id = _id;
        }
        public TextHtmlProcessor method;
        public int id;
    }
    private ArrayList<Pair> methods;
    private Deque<Node> stack;
    private Deque<Node> stack_state;
    private int last_id = 0;
}
