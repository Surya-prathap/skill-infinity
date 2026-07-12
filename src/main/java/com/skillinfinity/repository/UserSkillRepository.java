package com.skillinfinity.repository;

import com.skillinfinity.entity.Skill;
import com.skillinfinity.entity.User;
import com.skillinfinity.entity.UserSkill;
import com.skillinfinity.enums.SkillType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    boolean existsByUserAndSkillAndSkillType(User user, Skill skill, SkillType skillType);

    List<UserSkill> findByUser(User user);

}