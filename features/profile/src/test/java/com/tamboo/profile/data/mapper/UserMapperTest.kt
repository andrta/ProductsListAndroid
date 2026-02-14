package com.tamboo.profile.data.mapper

import com.tamboo.profile.data.model.AddressDto
import com.tamboo.profile.data.model.NameDto
import com.tamboo.profile.data.model.UserDto
import org.junit.Assert.assertEquals
import org.junit.Test

class UserMapperTest {
    @Test
    fun `toDomain maps UserDto to User correctly`() {
        val dto = getMockDto()

        val result = dto.toDomain()

        assertEquals(dto.id, result.id)
        assertEquals("Andrea Tamburrino", "${result.firstName} ${result.lastName}")
        assertEquals("Barcelona", result.address.city)
    }
}

private fun getMockDto() = UserDto(
        id = 8,
        email = "test@example.com",
        username = "Tamboo",
        name = NameDto("Andrea", "Tamburrino"),
        address = AddressDto("Barcelona", "Carrer de la Paz", 10, "08001"),
        phone = "123456"
)
