package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepositoryOld;

import java.util.*;

@Repository
@Slf4j
public class ItemRepositoryInMemory implements ItemRepositoryOld {

    private UserRepositoryOld userRepositoryOld;
    private Map<Integer, Item> items = new HashMap<>();
    private Integer itemId = 0;

    public ItemRepositoryInMemory(UserRepositoryOld userRepositoryOld) {
        this.userRepositoryOld = userRepositoryOld;
    }

    private Integer getItemId() {
        return ++itemId;
    }

    private void validateOnAdd(Item item) {
        if (!userRepositoryOld.isPresent(item.getOwner())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (item.getAvailable() == null || item.getName().equals("") || item.getDescription() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private void validateOnUpdate(Item item, Integer id) {
        if (items.get((int) id) == null
                || (int) items.get((int) id).getOwner() != (int) item.getOwner()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private void validateOnDelete(Integer itemId, Integer userId) {
        if (items.get((int) itemId) == null || items.get((int) itemId).getOwner() != (int) userId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Item add(Item item) {
        validateOnAdd(item);
        item.setId(getItemId());
        items.put(item.getId(), item);
        log.info("добавлена вещь /{}/", item.toString());
        return item;
    }

    @Override
    public Item update(Item item, Integer id) {
        validateOnUpdate(item, id);
        Item itemUpd = items.get(id);
        if (item.getName() != null) {
            itemUpd.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemUpd.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemUpd.setAvailable(item.getAvailable());
        }

        items.put(id, itemUpd);
        log.info("обновлена вещь /{}/", items.get(id).toString());
        return items.get(id);
    }

    @Override
    public Item getById(Integer id) {
        log.info("запрошена вещь /id={}/", id);
        return items.get(id);
    }

    @Override
    public Collection<Item> getAll(Integer userId) {
        ArrayList<Item> itemList = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() == (int) userId) {
                itemList.add(item);
            }
        }
        log.info("запрошен список вещей владельца /id={}/", userId);
        return itemList;
    }

    @Override
    public Collection<Item> getByNameOrDesc(String text) {
        ArrayList<Item> itemList = new ArrayList<>();
        if (text == null || text.equals("")) {
            return itemList;
        }
        String[] split;
        boolean mismatch;
        for (Item item : items.values()) {
            if (!item.getAvailable()) {
                continue;
            }
            mismatch = true;
            split = item.getName().split(" ");
            for (int i = 0; i < split.length; i++) {
                if (split[i].length() >= text.length()
                        && split[i].substring(0, text.length()).toLowerCase().equals(text.toLowerCase())) {
                    itemList.add(item);
                    mismatch = false;
                    break;
                }
            }
            if (!mismatch) {
                continue;
            }

            split = item.getDescription().split(" ");
            for (int i = 0; i < split.length; i++) {
                if (split[i].length() >= text.length()
                        && split[i].substring(0, text.length()).toLowerCase().equals(text.toLowerCase())) {
                    itemList.add(item);
                    break;
                }
            }
        }
        log.info("запрошен поиск вещи по строке /text={}/, найдено {}", text, itemList.size());
        return itemList;
    }

    @Override
    public void delete(Integer itemId, Integer userId) {
        validateOnDelete(itemId, userId);
        items.remove(itemId);
        log.info("удалена вещь /id={}/", itemId);
    }

}
