package io.github.syst3ms.skriptparser.variables;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

class VariableMap {
    private static final Pattern listSplitPattern = Pattern.compile(Pattern.quote(Variables.LIST_SEPARATOR));
    private Map<String, Object> map = new HashMap<>(); // Ordering is not important right now

    private static String[] splitList(String name) {
        return listSplitPattern.split(name);
    }

    /**
	 * Sets a variable.
	 *
	 * @param name  The variable's name. Can be a "list variable::*" (<tt>value</tt> must be <tt>null</tt> in this case)
	 * @param value The variable's value. Use <tt>null</tt> to delete the variable.
	 */
    @SuppressWarnings("unchecked")
    public void setVariable(String name, @Nullable Object value) {
        if (!name.endsWith("*")) {
            if (value == null) {
                map.remove(name);
            } else {
                map.put(name, value);
            }
        }
        String[] split = splitList(name);
        Map<String, Object> parent = map;
        for (int i = 0; i < split.length; i++) {
            String n = split[i];
            Object current = parent.get(n);
            if (current == null) {
                if (i == split.length - 1) {
                    if (value != null) {
                        parent.put(n, value);
                    }
                    break;
                } else if (value != null) {
                    parent.put(n, current = new HashMap<>());
                    parent = (Map<String, Object>) current;
                } else {
                    break;
                }
            } else if (current instanceof Map) {
                if (i == split.length - 1) {
                    if (value == null) {
                        ((Map<String, Object>) current).remove(null);
                    } else {
                        ((Map<String, Object>) current).put(null, value);
                    }
                    break;
                } else if (i == split.length - 2 && split[i + 1].equals("*")) {
                    assert value == null;
                    deleteFromHashMap(String
                        .join(Variables.LIST_SEPARATOR, Arrays.copyOfRange(split, 0, i + 1)), (Map<String, Object>) current);
                    Object v = ((Map<String, Object>) current).get(null);
                    if (v == null) {
                        parent.remove(n);
                    } else {
                        parent.put(n, v);
                    }
                    break;
                } else {
                    parent = (Map<String, Object>) current;
                }
            } else {
                if (i == split.length - 1) {
                    if (value == null) {
                        parent.remove(n);
                    } else {
                        parent.put(n, value);
                    }
                    break;
                } else if (value != null) {
                    Map<String, Object> c = new HashMap<>();
                    c.put(null, current);
                    parent.put(n, c);
                    parent = c;
                } else {
                    break;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void deleteFromHashMap(String parent, Map<String, Object> current) {
        for (Map.Entry<String, Object> e : current.entrySet()) {
            if (e.getKey() == null) {
                continue;
            }
            map.remove(parent + Variables.LIST_SEPARATOR + e.getKey());
            Object val = e.getValue();
            if (val instanceof Map) {
                deleteFromHashMap(parent + Variables.LIST_SEPARATOR + e.getKey(), (Map<String, Object>) val);
            }
        }
    }

    /**
	 * Returns the internal value of the requested variable.
	 * <p>
	 * <b>Do not modify the returned value!</b>
	 *
	 * @param name
	 * @return an Object for a normal Variable or a Map<String, Object> for a list variable, or null if the variable is not set.
	 */
    @SuppressWarnings("unchecked")
    @Nullable
    public Object getVariable(String name) {
        if (!name.endsWith("*")) {
            return map.get(name);
        } else {
            String[] split = splitList(name);
            Map<String, Object> current = map;
            for (int i = 0; i < split.length; i++) {
                String n = split[i];
                if (n.equals("*")) {
                    assert i == split.length - 1;
                    return current;
                }
                Object o = current.get(n);
                if (o == null) {
                    return null;
                }
                if (o instanceof Map) {
                    current = (Map<String, Object>) o;
                    assert i != split.length - 1;
                } else {
                    return null;
                }
            }
            return null;
        }
    }
}
