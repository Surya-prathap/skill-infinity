package com.skillinfinity.repository;

import com.skillinfinity.entity.Skill;
import com.skillinfinity.entity.User;
import com.skillinfinity.entity.UserSkill;
import com.skillinfinity.enums.SkillType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    boolean existsByUserAndSkillAndSkillType(User user, Skill skill, SkillType skillType);

    List<UserSkill> findByUser(User user);

    List<UserSkill> findByUserAndSkillType(User user, SkillType skillType);

    List<UserSkill> findBySkillAndSkillType(Skill skill, SkillType skillType);

    Optional<UserSkill> findByUserAndSkillAndSkillType(
            User user,
            Skill skill,
            SkillType skillType
    );

}