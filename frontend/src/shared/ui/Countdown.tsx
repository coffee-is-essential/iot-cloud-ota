import { useState, useEffect, JSX } from "react";

/**
 * Countdown 컴포넌트의 props
 */
interface CountdownProps {
  expiresAt: Date;
}

/**
 * 주어진 만료 시간까지 남은 시간을 분:초 형식으로 표시하는 카운트다운 컴포넌트
 * @param {CountdownProps} props - 컴포넌트 props
 * @returns {JSX.Element} 남은 시간을 표시하는 JSX 요소
 */
const Countdown = ({ expiresAt }: CountdownProps): JSX.Element => {
  const calculateTimeLeft = () => {
    const difference = +new Date(expiresAt) - +new Date();
    let timeLeft = {};

    if (difference > 0) {
      timeLeft = {
        minutes: Math.floor((difference / 1000 / 60) % 60),
        seconds: Math.floor((difference / 1000) % 60),
      };
    }

    return timeLeft;
  };

  const [timeLeft, setTimeLeft] = useState(calculateTimeLeft);

  useEffect(() => {
    const timer = setTimeout(() => {
      setTimeLeft(calculateTimeLeft);
    }, 1000);

    return () => clearTimeout(timer);
  });

  const timerComponents: JSX.Element[] = [];

  Object.keys(timeLeft).forEach((interval) => {
    if (!timeLeft[interval as keyof typeof timeLeft]) {
      return;
    }

    timerComponents.push(
      <span>
        {String(timeLeft[interval as keyof typeof timeLeft]).padStart(2, "0")}
      </span>,
    );
  });

  return (
    <div>
      {timerComponents.length ? (
        timerComponents.reduce((prev, curr) => (
          <>
            {prev} : {curr}
          </>
        ))
      ) : (
        <span>인증 시간이 만료되었습니다.</span>
      )}
    </div>
  );
};

export default Countdown;
