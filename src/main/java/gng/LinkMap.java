package gng;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class LinkMap {

    private Map<GasNeuron, Set<Link>> linkMap;

    public LinkMap() {
        linkMap = new HashMap<>();
    }

    public void addToKey(GasNeuron key, GasNeuron element) {
        final Set<Link> links = linkMap.getOrDefault(key, new HashSet<>());
        final Link link = new Link(key, element);
        links.remove(link);
        links.add(link);
        linkMap.put(key, links);
        final Set<Link> secondLinks = linkMap.getOrDefault(element, new HashSet<>());
        secondLinks.add(link);
        linkMap.put(element, secondLinks);
    }

    public void incAllByKey(GasNeuron key) {
        Optional.ofNullable(linkMap.get(key))
                .ifPresent(links -> links.forEach(Link::incAge));
    }

    public void removeByKeyAndAge(GasNeuron key, int maxAge) {
        Optional.ofNullable(linkMap.get(key))
                .ifPresent(links -> links.removeAll(
                        links.stream().filter(link -> link.getAge() > maxAge).collect(Collectors.toSet())));
    }

    public Set<GasNeuron> allByKey(GasNeuron key) {
        return linkMap.getOrDefault(key, new HashSet<>())
                      .stream()
                      .map(link -> link.notLike(key))
                      .collect(Collectors.toSet());
    }

    public Set<GasNeuron> checkNeurons(List<GasNeuron> list) {
        list.removeAll(linkMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));
        list.removeAll(linkMap.entrySet().stream().filter(entry -> entry.getValue().size() != 0)
                              .map(Map.Entry::getKey)
                              .collect(Collectors.toSet()));
        return new HashSet<>(list);
    }

    public void removeLink(GasNeuron first, GasNeuron second) {
        removeByKey(first, second);
        removeByKey(second, first);
    }

    private void removeByKey(GasNeuron key, GasNeuron element) {
        final Set<Link> links = linkMap.getOrDefault(key, new HashSet<>());
        links.removeAll(links.stream().filter(link -> link.getSecond().equals(element)).collect(Collectors.toSet()));
        linkMap.put(key, links);
    }

}
