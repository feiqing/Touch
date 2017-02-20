package com.vdian.touch.cases;

import java.util.*;

/**
 * @author jifang
 * @since 2016/10/28 上午11:55.
 */
public interface TouchTestCase {

    String emptyParam();

    Map<String, Object> basicTypes(boolean a, byte b, short c, int d, char e, float f, double g, long h);

    String constumObject(User user, Date date, List<User> users);

    String converterObjectTypes(String string, Calendar calendar, Map<String, Object> map, List<String> singleList);

    void enumTypes(BlackType blackType, List<BlackType> blackTypes);

    String listSet(List list, Set set);

    class User {

        private String name;

        private int age;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
