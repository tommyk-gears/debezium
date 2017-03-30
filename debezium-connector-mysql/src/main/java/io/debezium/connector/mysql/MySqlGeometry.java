/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mysql;

import org.apache.commons.lang3.ArrayUtils;

import mil.nga.wkb.geom.Point;
import mil.nga.wkb.io.ByteReader;
import mil.nga.wkb.io.WkbGeometryReader;

/**
 * A parser API for MySQL Geometry types, it uses geopackage-wkb-java as a base for parsing Well-Known Binary
 *
 * @author oalsafi
 * @since 05.04.17.
 */
public class MySqlGeometry {

    private final byte[] wkb;

    /**
     * Create a MySqlGeometry using the supplied wkb, note this should be the cleaned wkb for MySQL
     *
     * @param wkb the Well-Known binary representation of the coordinate in the standard format
     */
    private MySqlGeometry(byte[] wkb) {
        this.wkb = wkb;
    }

    /**
     * Create a MySqlGeometry from the original byte array from MySQL binglog event
     *
     * @param mysqlBytes he original byte array from MySQL binglog event
     *
     * @return a {@link MySqlGeometry} which represents a MySqlGeometry API
     */
    public static MySqlGeometry fromBytes(final byte[] mysqlBytes) {
        return new MySqlGeometry(convertToWkb(mysqlBytes, 4));
    }

    /**
     * Returns the standard well-known binary representation of the MySQL byte
     *
     * @return {@link byte[]} which represents the standard well-known binary
     */
    public byte[] getWkb() {
        return wkb;
    }

    /**
     * It returns a Point coordinate according to OpenGIS based on the WKB
     *
     * @return {@link Point} point coordinate
     */
    public Point getPoint() {
        return (Point) WkbGeometryReader.readGeometry(new ByteReader(wkb));
    }

    /**
     * Since MySQL appends 4 bytes as type prefix, we shrink the byte array 4 times in order to have a valid WKB
     * representation
     *
     * @param source      the original byte array from MySQL binglog event
     * @param numOfShifts the number of times we shall shrink the array
     *
     * @return a {@link byte[]} which represents the standard well-known binary
     */
    private static byte[] convertToWkb(byte[] source, int numOfShifts) {
        if (numOfShifts > 0) {
            int shiftedIndex = numOfShifts - 1;
            return convertToWkb(ArrayUtils.remove(source, shiftedIndex), shiftedIndex);
        } else {
            return source;
        }
    }
}
