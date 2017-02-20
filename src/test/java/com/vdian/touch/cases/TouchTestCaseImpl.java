package com.vdian.touch.cases;

import com.vdian.touch.Touch;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author jifang
 * @since 2016/10/28 上午11:55.
 */
@Component
public class TouchTestCaseImpl implements TouchTestCase {

    @Touch
    private String p1;

    @Touch
    private Date p2;

    @Touch
    private Map<String, Object> p3;

    @Touch
    private Set<String> p4;

    @Touch
    private int p5;

    @Touch
    private char p6;

    @Touch
    private User user;

    @Touch
    private List<User> users;

    @Touch
    public String emptyParam() {
        System.out.println("emptyParam");
        return "emptyParam";
    }

    @Touch
    public Map<String, Object> basicTypes(boolean a, byte b, short c, int d, char e, float f, double g, long h) {
        Map<String, Object> map = new HashMap<>();
        map.put("a", a);
        map.put("b", b);
        map.put("c", c);
        map.put("d", d);
        map.put("e", e);
        map.put("f", f);
        map.put("g", g);
        map.put("h", h);
        return map;
    }

    @Touch
    public String constumObject(User user, Date date, List<User> users) {
        StringBuilder sb = new StringBuilder(user.getName() + user.getAge());
        for (User u : users) {
            sb.append("name= ").append(u.getName()).append(", ").append(u.getAge());
        }
        sb.append(date);
        return sb.toString();
    }


    @Touch
    public String converterObjectTypes(String string, Calendar calendar, Map<String, Object> map, List<String> singleList) {
        return string + calendar + map + singleList;
    }

    @Touch
    public void enumTypes(BlackType blackType, List<BlackType> blackTypes) {
        System.out.println(blackType);
        System.out.println(blackTypes);
    }

    @Touch
    public String listSet(List list, Set set) {
        return list + "" + set;
    }
}
