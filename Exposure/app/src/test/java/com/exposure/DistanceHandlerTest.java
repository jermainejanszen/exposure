package com.exposure;

import com.exposure.user.CurrentUser;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static com.exposure.handlers.DistanceHandler.*;

public class DistanceHandlerTest {
    private CurrentUser userA, userB;

    @Before
    public void setup() {
        userA = new CurrentUser("0");
        userB = new CurrentUser("1");
    }

    @Test
    public void nullUserATest() {
        boolean caughtException = false;

        try {
            distanceInKM(null, userB);
        } catch (IllegalArgumentException ignored) {
            caughtException = true;
        }

        assertTrue(caughtException);
    }

    @Test
    public void nullUserBTest() {
        boolean caughtException = false;

        try {
            distanceInKM(userA, null);
        } catch (IllegalArgumentException ignored) {
            caughtException = true;
        }

        assertTrue(caughtException);
    }

    @Test
    public void nullUserAUserBTest() {
        boolean caughtException = false;

        try {
            distanceInKM(null, null);
        } catch (IllegalArgumentException ignored) {
            caughtException = true;
        }

        assertTrue(caughtException);
    }

    @Test
    public void userAuserBLocationsNotSetTest() {
        assertEquals(10000, distanceInKM(userA, userB));
    }

    @Test
    public void userALocationNotSetTest() {
        userB.setLocation(10, 10);
        assertEquals(10000, distanceInKM(userA, userB));
    }

    @Test
    public void userBLocationNotSetTest() {
        userA.setLocation(10, 10);
        assertEquals(10000, distanceInKM(userA, userB));
    }

    @Test
    public void zeroKMDistanceTest() {
        userA.setLocation(10, 10);
        userB.setLocation(10, 10);
        assertEquals(0, distanceInKM(userA, userB));
    }

    @Test
    public void twoKMDistanceTest() {
        userA.setLocation(10, 10);
        userB.setLocation(10.015, 10.015);
        assertEquals(2, distanceInKM(userA, userB));
    }

    @Test
    public void threeKMDistanceTest() {
        userA.setLocation(10, 10);
        userB.setLocation(10.02, 10.02);
        assertEquals(3, distanceInKM(userA, userB));
    }

    @Test
    public void fiveKMDistanceTest() {
        userA.setLocation(10, 10);
        userB.setLocation(10.03, 10.03);
        assertEquals(5, distanceInKM(userA, userB));
    }

    @Test
    public void sixKMDistanceTest() {
        userA.setLocation(10, 10);
        userB.setLocation(10.036, 10.036);
        assertEquals(6, distanceInKM(userA, userB));
    }

    @Test
    public void eightKMDistanceTest() {
        userA.setLocation(10, 10);
        userB.setLocation(10.05, 10.05);
        assertEquals(8, distanceInKM(userA, userB));
    }

    @Test
    public void nineKMDistanceTest() {
        userA.setLocation(10, 10);
        userB.setLocation(10.055, 10.055);
        assertEquals(9, distanceInKM(userA, userB));
    }

    @Test
    public void fifteenKMDistanceTest() {
        userA.setLocation(10, 10);
        userB.setLocation(10.095, 10.095);
        assertEquals(15, distanceInKM(userA, userB));
    }

    @Test
    public void twentyKMDistanceTest() {
        userA.setLocation(10, 10);
        userB.setLocation(10.13, 10.13);
        assertEquals(20, distanceInKM(userA, userB));
    }

    @Test
    public void fiftyKMDistanceTest() {
        userA.setLocation(10, 10);
        userB.setLocation(10.32, 10.32);
        assertEquals(50, distanceInKM(userA, userB));
    }

    @Test
    public void oneHundredKMDistanceTest() {
        userA.setLocation(10, 10);
        userB.setLocation(10.64, 10.64);
        assertEquals(100, distanceInKM(userA, userB));
    }
}