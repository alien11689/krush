package pl.touk.exposed.one2many.uni

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.Test
import java.util.*

class CustomerTest {

    @Before
    fun connect() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
    }

    @Test
    fun shouldInsertAndSelectUniOneToMany() {
        transaction {
            SchemaUtils.create(CustomerTable, PhoneTable, AddressTable)

            // given
            val customer = Customer(name = "TouK", age = 13).let { customer ->
                val customerId = CustomerTable.insert { it.from(customer) }[CustomerTable.id]
                customer.copy(id = customerId)
            }

            val phone = Phone(number = "777 888 999").let { phone ->
                val phoneId = PhoneTable.insert { it.from(phone, customer) }[PhoneTable.id]
                phone.copy(id = phoneId)
            }

            val address = Address(id = UUID.randomUUID(), city = "Warsaw", street = "Suwak", houseNo = "12/14", apartmentNo = "206").let { address ->
                AddressTable.insert { it.from(address, customer) }[AddressTable.id]
                address
            }

            // then
            val customers = (CustomerTable leftJoin PhoneTable leftJoin AddressTable).selectAll().toCustomerList()

            // then
            val fullCustomer = customer.copy(addresses = listOf(address), phones = listOf(phone))
            assertThat(customers).containsOnly(fullCustomer)
        }
    }
}
