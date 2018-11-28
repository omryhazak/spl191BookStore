package bgu.spl.mics.application.passiveObjects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.omg.PortableInterceptor.SUCCESSFUL;

import static org.junit.Assert.*;

public class InventoryTest {

    /** Object under test. */
    private Inventory inv;

    /**
     * Test method for
     * @throws Exception
     */

    @Before public void setUp() throws Exception {
        inv = Inventory.getInstance();
    }

    @After public void tearDown() throws Exception {
        inv = null;
    }

    @Test public void getInstance() {
        assertEquals(Inventory.getInstance(), inv);
    }

    /**
     * positive test for Inventory.load
     */

    @Test public void load() {
        //creates anonymous books for test
        BookInventoryInfo book1 = new BookInventoryInfo() {
            @Override
            public String getBookTitle(){
                return "1";
            }
            @Override
            public int getAmountInInventory() {
                return 1;
            }
            @Override
            public int getPrice() {
                return 100;
            }
        };
        BookInventoryInfo book2 = new BookInventoryInfo() {
            @Override
            public String getBookTitle(){
                return "2";
            }
            @Override
            public int getAmountInInventory() {
                return 1;
            }
            @Override
            public int getPrice() {
                return 150;
            }
        };

        //loads the books to inventory
        BookInventoryInfo[] inventory = new BookInventoryInfo[2];
        inventory[0] = book1;
        inventory[1] = book2;
        inv.load(inventory);
        assertEquals(inv.checkAvailabiltyAndGetPrice(book1.getBookTitle()), book1.getPrice());
        inv.take(book1.getBookTitle());
        assertEquals(inv.checkAvailabiltyAndGetPrice(book2.getBookTitle()), book2.getPrice());
        inv.take(book2.getBookTitle());
    }

    /**
     * positive test for Inventory.take
     */
    @Test public void take() {

        //positive test
        //creates anonymous books for test
        BookInventoryInfo book1 = new BookInventoryInfo() {
            @Override
            public String getBookTitle() {
                return "1";
            }

            @Override
            public int getAmountInInventory() {
                return 2;
            }

            @Override
            public int getPrice() {
                return 100;
            }
        };


        //loads two books to inventory and checks if after take them there are no more books of this kind
        BookInventoryInfo[] inventory = new BookInventoryInfo[2];
        inventory[0] = book1;
        inv.load(inventory);
        assertEquals(inv.take(book1.getBookTitle()), OrderResult.SUCCESSFULLY_TAKEN);
        assertEquals(inv.checkAvailabiltyAndGetPrice(book1.getBookTitle()), book1.getPrice());
        assertEquals(inv.take(book1.getBookTitle()), OrderResult.SUCCESSFULLY_TAKEN);
        assertEquals(inv.checkAvailabiltyAndGetPrice(book1.getBookTitle()), (-1));
    }

    /**
     * negative test for Inventory.take
     */
    @Test public void takeNegative() {
        //creates anonymous books for test
        BookInventoryInfo book1 = new BookInventoryInfo() {
            @Override
            public String getBookTitle() {
                return "1";
            }

            @Override
            public int getAmountInInventory() {
                return 2;
            }

            @Override
            public int getPrice() {
                return 100;
            }
        };
        assertEquals(inv.take(book1.getBookTitle()), OrderResult.NOT_IN_STOCK);
    }

    /**
     * positive test for Inventory.checkAvailabiltyAndGetPrice
     */
    @Test public void checkAvailabiltyAndGetPrice() {
        //creates anonymous books for test
        BookInventoryInfo book1 = new BookInventoryInfo() {
            @Override
            public String getBookTitle() {
                return "1";
            }

            @Override
            public int getAmountInInventory() {
                return 1;
            }

            @Override
            public int getPrice() {
                return 100;
            }
        };
        //loads two books to inventory and checks if after take them there are no more books of this kind
        BookInventoryInfo[] inventory = new BookInventoryInfo[2];
        inventory[0] = book1;
        inv.load(inventory);
        assertEquals(inv.checkAvailabiltyAndGetPrice(book1.getBookTitle()), book1.getPrice());
        inv.take(book1.getBookTitle());

    }

    /**
     * positive test for Inventory.printInventoryToFile
     */
    @Test public void printInventoryToFile() {

    }
}