package org.kcsmini2.ojeommo.member.service;

import lombok.RequiredArgsConstructor;
import org.kcsmini2.ojeommo.category.entity.FavoriteCategory;
import org.kcsmini2.ojeommo.config.jwt.JwtProvider;
import org.kcsmini2.ojeommo.member.data.dto.SignRequest;
import org.kcsmini2.ojeommo.member.data.dto.SignResponse;
import org.kcsmini2.ojeommo.member.data.entity.Authority;
import org.kcsmini2.ojeommo.member.data.entity.Member;
import org.kcsmini2.ojeommo.member.repository.MemberRepository;
import org.kcsmini2.ojeommo.member.repository.FavoriteCategoryRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

/**
 * 작성자: 김준연
 *
 * 설명: member CRUD + a 서비스
 *
 * 최종 수정 일자: 2023/07/24
 */
@Service
@Transactional
@RequiredArgsConstructor
@EnableWebSecurity
public class SignService {

    private final MemberRepository memberRepository;
    private final FavoriteCategoryRepository favoriteCategoryRepository;
    private final JwtProvider jwtProvider;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean register(SignRequest request) throws Exception {
        try {
            Member member = Member.builder()
                    .id(request.getId())
                    .pw(passwordEncoder.encode(request.getPw()))
                    .name(request.getName())
                    .nickname(request.getNickname())
                    .email(request.getEmail())
                    .build();

            member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));

            memberRepository.save(member);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("잘못된 요청입니다.");

        }
        try {
            for(String str : request.getCategoryIds()) {
                Long id = Long.parseLong(str);
                FavoriteCategory favoriteCategory = FavoriteCategory.builder()
                        .categoryId(id)
                        .memberId(request.getId())
                        .build();
                favoriteCategoryRepository.save(favoriteCategory);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("catgoryIds가 비어있음.");
        }


        return true;
    }

    public SignResponse login(SignRequest request) throws Exception {
        Member member = memberRepository.findById(request.getId()).orElseThrow(() ->
                new BadCredentialsException("잘못된 계정정보입니다."));

        if (!passwordEncoder.matches(request.getPw(), member.getPw())) {
            throw new BadCredentialsException("잘못된 계정정보입니다.");
        }

        return SignResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .roles(member.getRoles())
                .token(jwtProvider.createToken(member.getId(), member.getRoles()))
                .build();

    }

    public boolean update(SignRequest request) {

        if (request.getPw() != null ){
            request.setPw(passwordEncoder.encode(request.getPw()));
        }

        Optional<Member> member = memberRepository.findById(request.getId());

        if(member.isPresent()) {
            return member.get().updateMember(request);
        }
        else return false;
    }

    public boolean delete(String id) {
        System.out.println("delete service");
        try {
            memberRepository.deleteById(id);
            System.out.println("deleted");
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

}