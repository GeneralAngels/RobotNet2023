package org.ga2230net;

public record PacketRepresentation(String header,
                                   String[] fields,
                                   String fmt,
                                   boolean singleInstance) {}
