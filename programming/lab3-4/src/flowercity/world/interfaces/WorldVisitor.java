package flowercity.world.interfaces;

import flowercity.world.locations.*;
import flowercity.world.vehicles.Steamboat;

/**
 * Посетитель мира, который может «осматривать» различные локации
 * и объекты по паттерну Visitor.
 */
public interface WorldVisitor {
    default void visit(River river) {}
    default void visit(City city) {}
    default void visit(Bridge bridge) {}
    default void visit(Road road) {}
    default void visit(Hill hill) {}
    default void visit(Steamboat steamboat) {}
}
