package io.laaf.restapi.user;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class UserDaoService {
    private static List<User> users = new ArrayList<>();

    private static int usersCount = 3;

    static {
        users.add(new User(1, "john",  new Date(), "pass1", "101010-1111111"));
        users.add(new User(2, "alice", new Date(), "pass2", "202020-2222222"));
        users.add(new User(3, "elena", new Date(), "pass3", "303030-3333333"));
    }

    public List<User> findAll() {
        return users;
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(++usersCount);
        }

        users.add(user);
        return user;
    }

    public User findOne(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    public User deleteById(int id) {
        Iterator<User> iterator = users.iterator();

        while (iterator.hasNext()) {
            User user = iterator.next();

            if (user.getId() == id) {
                iterator.remove();
                return user;
            }
        }

        return null;
    }

    public User updateById(User findUser, User setUser) {

        findUser.setName(setUser.getName());
        findUser.setJoinDate(setUser.getJoinDate());

        return findUser;
    }
}
