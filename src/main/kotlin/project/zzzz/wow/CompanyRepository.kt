package project.zzzz.wow

import org.springframework.data.jpa.repository.JpaRepository

interface CompanyRepository : JpaRepository<SvcCompany, Long>
