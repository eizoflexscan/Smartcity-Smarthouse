package il.ac.technion.cs.smarthouse.system.user_information;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;

import il.ac.technion.cs.smarthouse.system.EmergencyLevel;

/**
 * This class saves all necessary information and implements the required API to
 * save information about the client's emergency contacts
 * 
 * @author Inbal Zukerman
 * @since Dec 29, 2016
 */

public class ContactsInformation {

    @Expose private final Map<EmergencyLevel, Map<String, Contact>> data = new HashMap<>();

    public ContactsInformation() {
        for (final EmergencyLevel elevel : EmergencyLevel.values())
            data.put(elevel, new HashMap<>());
    }

    /**
     * Adds a contact with a specific emergency level
     * 
     * @param c
     *            contact to add
     * @param elevel
     *            emergency level to inform this contact at
     */
    public void addContact(final Contact c, final EmergencyLevel elevel) {
        data.get(elevel).put(c.getId(), c);

    }

    /**
     * @param id
     *            the id of the contact required
     * @return the contact with the required id or null if does not exist
     */
    public Contact getContact(final String id) {
        for (final EmergencyLevel $ : EmergencyLevel.values())
            if (data.get($).containsKey(id))
                return data.get($).get(id);
        return null;
    }

    public void setContactEmergencyLevel(final String id, final EmergencyLevel newELevel) {
        for (final EmergencyLevel $ : EmergencyLevel.values())
            if (data.get($).containsKey(id)) {
                final Contact contact = data.get($).get(id);
                data.get($).remove(id);
                data.get(newELevel).put(id, contact);

            }

    }

    public void removeContact(final String id) {
        for (final EmergencyLevel $ : EmergencyLevel.values())
            if (data.get($).containsKey(id))
                data.get($).remove(id);

    }

    /**
     * @param elvl
     *            emergency level of the required contacts
     * @return list of all the contacts to be informed at required emergency
     *         level
     */
    public List<Contact> getContacts(final EmergencyLevel elvl) {
        final Map<String, Contact> temp = data.get(elvl);
        final ArrayList<Contact> $ = new ArrayList<>();
        for (final Contact ¢ : temp.values())
            $.add(¢);

        return $;
    }

    /** @return all the contacts saved in this instance */
    public List<Contact> getContacts() {
        final ArrayList<Contact> $ = new ArrayList<>();
        Map<String, Contact> temp;

        for (final EmergencyLevel elvl : EmergencyLevel.values()) {
            temp = data.get(elvl);
            $.addAll(temp.values());
        }

        return $;
    }

    // For debug mainly, leaving it implemented for future use
    @Override
    public String toString() {
        String $ = "";
        Map<String, Contact> temp;

        for (final EmergencyLevel elvl : EmergencyLevel.values()) {
            temp = data.get(elvl);
            if (temp.isEmpty())
                continue;
            $ += "Elvl is: " + elvl;

            for (final Contact ¢ : temp.values())
                $ += "\n\t" + ¢;

            $ += "\n";
        }

        return $;
    }
}
