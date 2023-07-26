package org.kcsmini2.ojeommo.board.service;

import jakarta.transaction.Transactional;
import org.kcsmini2.ojeommo.board.data.MemberDTO;
import org.kcsmini2.ojeommo.board.data.dto.request.bumped.GatherBoardBumpedRequestDTO;
import org.kcsmini2.ojeommo.board.data.dto.request.create.BoardCreateRequestDTO;
import org.kcsmini2.ojeommo.board.data.dto.response.detail.BoardDetailResponseDTO;
import org.springframework.stereotype.Service;

@Service
@Transactional
public interface GatherBoardService {


    public void createBoard(BoardCreateRequestDTO requestDTO, MemberDTO memberDTO);

    public BoardDetailResponseDTO readBoard(Long boardId, MemberDTO memberDTO);

    public void deleteBoard(Long boardId, MemberDTO memberDTO);

    public void bumpBoard(GatherBoardBumpedRequestDTO gatherBoardBumpedRequestDTO, Long boardId, MemberDTO memberDTO);

}