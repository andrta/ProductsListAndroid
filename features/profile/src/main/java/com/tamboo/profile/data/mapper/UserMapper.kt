package com.tamboo.profile.data.mapper

import com.tamboo.profile.data.model.UserDto
import com.tamboo.profile.domain.model.User
import com.tamboo.profile.domain.model.UserAddress

fun UserDto.toDomain(): User {
    return User(
        id = this.id,
        email = this.email,
        username = this.username,
        firstName = this.name.firstname,
        lastName = this.name.lastname,
        phone = this.phone,
        address = UserAddress(
            city = this.address.city,
            street = this.address.street,
            number = this.address.number,
            zipcode = this.address.zipcode
        )
    )
}
